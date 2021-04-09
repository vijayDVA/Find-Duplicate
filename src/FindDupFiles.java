import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.io.FilenameUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FindDupFiles 
{
	public static Set<String> emptyFiles = new HashSet<String>();
	public static Set<String> exceptns = new HashSet<String>();
	public static Map<String,Set<String>> finalMap = new HashMap<String,Set<String>>();
	
	/*static com.sun.management.OperatingSystemMXBean mxbean  =  (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	static long RamSize= Math.round((double)(((mxbean.getTotalPhysicalMemorySize())/1024)/1024)/1024);
	static double Part= RamSize/4.5;
	static long Partsize= (long) (Part*1024*1024*1024);*/
	
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
		FindDupDao Dao = new FindDupDao();
		Map<Long, HashMap<String,List<String>>> lists = new HashMap<Long, HashMap<String,List<String>>>();
	        

			System.out.println("C started ...");
			parseAllFiles(lists,"C:\\");
	
			System.out.println("D started ...");
			parseAllFiles(lists,"D:\\");
			
			System.out.println("E started ...");
			parseAllFiles(lists,"E:\\");

			
			for (String name: finalMap.keySet())
			{
			    String value = finalMap.get(name).toString();
			    System.out.println(value);
			}
			
			 System.out.println("SQL Started ..");
		      try {
					Dao.insert(finalMap);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			
			System.out.println("Executed Successfully..");
			System.out.println();
			
			if(!exceptns.isEmpty())
			{
		      System.out.println("These files are used by others ..");
		       for (String value : exceptns)
		            System.out.println(value + ", ");
		    }
		       
		   if(!emptyFiles.isEmpty()) {    
		   System.out.println("Empty Files are: ");
		       for (String value : emptyFiles)
		            System.out.println(value + ", ");
		   }
		   System.out.println(finalMap.size() + ", ");
	   }


	private static void parseAllFiles(Map<Long, HashMap<String, List<String>>> lists, String parentDirectory) throws Exception 
	{
		File[] filesInDirectory = new File(parentDirectory).listFiles();
        if(filesInDirectory != null) 
        {
        	for (File dirChild : filesInDirectory)
	        { 
        		if(dirChild.isDirectory())
	        	{
                parseAllFiles(lists,dirChild.getAbsolutePath());
	        	}
        		else 
        		{
        			try
        			{
        				System.out.println(dirChild.getAbsolutePath());
	        			long size = dirChild.length();
	        			if(size!=0)
	        			{
	        				String ext = FilenameUtils.getExtension(dirChild.getAbsolutePath());
	        				if(ext=="")
	        					ext="txxt";
	        				HashMap<String,List<String>> extMap = lists.get(size);
	        				List<String> paths;
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
	        					
        						RandomAccessFile raf2 = new RandomAccessFile(dirChild.getAbsoluteFile(), "r");
        						byte[] buffer2 = new byte[100];
        						byte[] buffer1 = new byte[100];
        						raf2.read(buffer2,0,100);
	        					for(String locations :paths)
	        					{ 
	        						RandomAccessFile raf1 = new RandomAccessFile(locations, "r");
	        						File locationFile = new File(locations);
	        						
	        				        
	        				        
	        				        raf1.read(buffer1,0,100);
	        				        if(!Arrays.equals(buffer1,buffer2))
	        				        	continue;
	        				        raf1.read(buffer1,0,100);
	        				        raf2.read(buffer2,0,100);
	        				        if(!Arrays.equals(buffer1,buffer2))
	        				        	continue;
	        				        
	        				        raf1.read(buffer1,0,100);
	        				        raf2.read(buffer2,0,100);
	        				        if(!Arrays.equals(buffer1,buffer2))
	        				        {
	        				        	continue;
	        				        }
	        				        else
	        				        {
	        				        	String uniqueFile2;
	        				        	if(314572800<size)
	        				        	{
	        				        	uniqueFile2 = makeHashLean(dirChild);
	        				        	}
	        				        	else
	        				        	{
	        				        	uniqueFile2 = makeHashQuick(dirChild);
	        				        	}
	        				        	if(finalMap.get(uniqueFile2) != null)
	        				        	{
	        				        		Set<String> dup = finalMap.get(uniqueFile2);
	        				        	    dup.add(dirChild.getAbsolutePath());	        				        		
	        				        		finalMap.put(uniqueFile2, dup);
	        				        		DupSts = false;
	        				        	}
	        				        	else 
	        				        	{
	        				        		String uniqueFile1;
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
		        				        		Set<String> dup = finalMap.get(uniqueFile1);
		        				        		if(dup == null)
		        				        		{
		        				        			dup = new HashSet<String>();
		        				        		}
	        				        			dup.add(locations);
		        				        		dup.add(dirChild.getAbsolutePath());
		        				        			        				        		
		        				        		finalMap.put(uniqueFile1, dup);
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
	        			else
	        			{
	        				emptyFiles.add(dirChild.getAbsolutePath());
	        			}
        			}
        			catch(Exception e)
					{			
						exceptns.add(e.toString() +" = "+dirChild);
						//throw e;
        				
					}
        		}
	        }
        }
        
        
	}


	private static String makeHashQuick(File infile) throws IOException 
	{
		  FileInputStream fin = new FileInputStream(infile);
	        byte data[] = new byte[(int) infile.length()];
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
