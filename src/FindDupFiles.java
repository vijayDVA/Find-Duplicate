import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;


public class FindDupFiles implements FileVisitor<Path>
{
	public static List<String> exceptns = new ArrayList<String>();
	public static Set<String> dup = new HashSet<String>();
	
	public static FindDupDao Dao = new FindDupDao();
	public static FindDupFiles ee = new FindDupFiles();
	public static Map<Long, Map<String,List<String>>> lists = new HashMap<Long, Map<String,List<String>>>();
	public static String ext;
	public static List<String> paths;
	RandomAccessFile raf1 ;
	RandomAccessFile raf2 ;
	byte[] buffer2 = new byte[100];
	byte[] buffer1 = new byte[100];
	public static File locationFile;
	public static String uniqueFile2;
	public static String uniqueFile1;
	
	private static MessageDigest messageDigest;
    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("cannot initialize MD5 hash function", e);
        }
    }

	public static void main(String[] args) throws Exception 
	{

			System.out.println("C started ...");
			parseAllFiles("C:\\");	
			
			System.out.println("D started ...");
			parseAllFiles("D:\\");
			
			System.out.println("E started ...");
			parseAllFiles("E:\\");	
	 }

		
	private static void parseAllFiles(String parentDirectory) throws Exception 
	{		
		Files.walkFileTree(Paths.get(parentDirectory), ee);
	}
	
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
       return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
    {
    
    	File dirChild = new File(file.toString());
    	if(dirChild.isDirectory())
    	{
        
    	}
		else 
		{
			try
			{
				System.out.println(dirChild.getAbsolutePath());
    			long size = dirChild.length();
    			if(size!=0)
    			{
    				ext = FilenameUtils.getExtension(dirChild.getAbsolutePath());
    				if(ext=="")
    					ext="txxt";
    				Map<String,List<String>> extMap = lists.get(size);
    				Boolean sts = true;
    				Boolean DupSts = true;
    				if(extMap==null)
    				{	        					
    					extMap = new HashMap<String,List<String>>();
    					paths = new ArrayList<String>();
    					paths.add(dirChild.getAbsolutePath());
    					extMap.put(ext, paths);
    					lists.put(size, extMap);
    					sts = false;
    				}
    				      				
    				if(lists.get(size).get(ext) == null)
    				{
    					paths = new ArrayList<String>();
    					paths.add(dirChild.getAbsolutePath());
    					extMap.put(ext, paths);
    					lists.put(size, extMap);
    					sts = false;
    				}
    				if(sts) 
    				{
    					paths = lists.get(size).get(ext);
    					//long pathsize = paths.size();
    					
						raf2 = new RandomAccessFile(dirChild.getAbsoluteFile(), "r");
					
						
    					for(String locations :paths)
    					{ 
    						raf1 = new RandomAccessFile(locations, "r");
    						locationFile = new File(locations);
    				        
    				        raf1.seek(0);
    				        raf2.seek(0);
    				        
    				        
    				        raf1.read(buffer1,0,100);
    				        raf2.read(buffer2,0,100);
    				        if(!Arrays.equals(buffer1,buffer2))
    				        	continue;
    				        
    				        /*raf1.seek(size/2);
    				        raf2.seek(size/2);
    				        
    				        raf1.read(buffer1,0,100);
    				        raf2.read(buffer2,0,100);
    				        if(!Arrays.equals(buffer1,buffer2))
    				        	continue;*/
    				        
    				        if(size>100)
    				        {
    				        raf1.seek(size-100);
    				        raf2.seek(size-100);
    				        
    				        raf1.read(buffer1,0,100);
    				        raf2.read(buffer2,0,100);
    				        }
    				        if(!Arrays.equals(buffer1,buffer2))
    				        {
    				        	continue;
    				        }
    				        else
    				        {
    				        	
    				        	if(314572800<size)//400MB
    				        	{
    				        	uniqueFile2 = makeHashLean(dirChild);
    				        	}
    				        	else
    				        	{
    				        	uniqueFile2 = makeHashQuick(dirChild);
    				        	}
    				        	if(dup.contains(uniqueFile2) == true)
    				        	{
    				        		Dao.update(uniqueFile2,dirChild.getAbsoluteFile());
    				        		DupSts = false;
    				        	}
    				        	else 
    				        	{
    				        		
        				        	if(314572800<size)
        				        	{	
        				        	uniqueFile1 = makeHashLean(locationFile);
        				        	}
        				        	else
        				        	{
        				        	uniqueFile1 = makeHashQuick(locationFile);
        				        	}
        				        	if(uniqueFile1.equals(uniqueFile2))
        				        	{
        				        		        				        		
    				        			Dao.add(locations,dirChild.getAbsolutePath(),uniqueFile1);
    				        			dup.add(uniqueFile1);
        				        		DupSts = false;
        				        	}
    				        	}
    				        }
    				        if(!DupSts)
    				        	break;
    				       
    					}
    					if(DupSts)
    					{
    					paths.add(dirChild.getAbsolutePath());
        				extMap.put(ext, paths);
        				lists.put(size, extMap);
        				}
    				}

    			}
    			
			}
			catch(Exception e)
			{			
				exceptns.add(e.toString() +" = "+dirChild);
				//throw e;
				
			}
		}
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        // This is important to note. Test this behaviour
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
       return FileVisitResult.CONTINUE;
    }

    private static String makeHashQuick(File infile) throws IOException 
	{
		    FileInputStream fin = new FileInputStream(infile);
	        byte data[] = new byte[ (int) infile.length()];
	        fin.read(data);
	        fin.close();
	        String hash = new BigInteger(1, messageDigest.digest(data)).toString(16);
	        return hash;
	}
	public static String makeHashLean(File infile) throws Exception
    {
        RandomAccessFile file = new RandomAccessFile(infile, "r");
        int buffSize = 52428800 ;
        byte[] buffer = new byte[buffSize];
        long read = 0;
        long offset = file.length();
        int unitsize;
        
        while (read < offset)
        {
            unitsize = (int) (((offset - read) >= buffSize) ? buffSize: (offset - read));            
            file.read(buffer, 0, unitsize);      
            messageDigest.update(buffer, 0, unitsize);
            read += unitsize;
        }

        file.close();
        String hash = new BigInteger(1, messageDigest.digest()).toString(16);
        return hash;
    }
	

}
