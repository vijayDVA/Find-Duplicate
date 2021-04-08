import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
	
	private static MessageDigest messageDigest;
    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("cannot initialize SHA-512 hash function", e);
        }
    }
    

	public static void main(String[] args) throws Exception 
	{
		//FindDupDao Dao = new FindDupDao();
		Map<Long, HashMap<String,List<String>>> lists = new HashMap<Long, HashMap<String,List<String>>>();
	      
			System.out.println("C started ...");
			parseAllFiles(lists,"E:\\");
	
			System.out.println("D started ...");
			//parseAllFiles(lists,"D:\\");
			
			System.out.println("E started ...");
			//parseAllFiles(lists,"E:\\");

			
			for (String name: finalMap.keySet()) {
			    String value = finalMap.get(name).toString();
			    System.out.println(name + " " + value);
			}
			System.out.println("Executed Successfully..");
		      System.out.println("These files are used by others ..");
		       for (String value : exceptns)
		            System.out.println(value + ", ");
		       
		       System.out.println("Empty Files: ");
		       for (String value : emptyFiles)
		            System.out.println(value + ", ");
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
	        					long pathsize = paths.size();
	        					for(String locations :paths)
	        					{ 
	        						RandomAccessFile raf1 = new RandomAccessFile(locations, "r");
	        						RandomAccessFile raf2 = new RandomAccessFile(dirChild.getAbsoluteFile(), "r");
	        						File locationFile = new File(locations);
	        						
	        				        byte[] buffer1 = new byte[100];
	        				        byte[] buffer2 = new byte[100];
	        				        raf1.read(buffer1,0,100);
	        				        raf2.read(buffer2,0,100);
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
	        				        	String uniqueFile1 = makeHashQuick(locationFile);
	        				        	String uniqueFile2 = makeHashQuick(dirChild);
	        				        	if(uniqueFile1.equals(uniqueFile2))
	        				        	{
	        				        		Set<String> dup = finalMap.get(uniqueFile1);
	        				        		if(dup == null)
	        				        		{
	        				        			dup = new HashSet<String>();
	        				        		}
	        				        		if(pathsize>1)
	        				        		{
	        				        			dup.add(dirChild.getAbsolutePath());
	        				        		}
	        				        		else
	        				        		{
	        				        			dup.add(locations);
		        				        		dup.add(dirChild.getAbsolutePath());
	        				        		}
	        				        		
	        				        		finalMap.put(uniqueFile1, dup);
	        				        		DupSts = false;
	        				        	}
	        				        }
	        				       
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

}
