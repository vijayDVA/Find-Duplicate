import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindDupDao  {

	private static String dbUrl = "jdbc:mysql://localhost:3306/duplists";
	private static String dbUname = "root";
	private static String dbPassword = "1234";
	private static String dbDriver = "com.mysql.cj.jdbc.Driver";
	public static Connection con = null;
	
	public static String sql1 = "UPDATE duplists.dupdb SET File_locations = concat(File_locations,?) where id=?";
	public static String sql2 = "INSERT INTO duplists.dupdb (id,File_locations) VALUES(?,?)";
	
	public static PreparedStatement ps1;
	public static PreparedStatement ps2;
	
    static{ 
    	try {
		Class.forName(dbDriver);
		con = DriverManager.getConnection(dbUrl, dbUname, dbPassword);
		System.out.println("Connected");
		ps1 = con.prepareStatement(sql1);
		ps2 = con.prepareStatement(sql2);
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}}	
	

	public void add(String locations, String absolutePath, String uniqueFile1) throws SQLException
	{	
		ps2.setString(1,uniqueFile1);
		ps2.setString(2, locations+','+absolutePath);
		ps2.executeUpdate();		
	}

	public void update(String uniqueFile2, File absoluteFile) throws SQLException
	{	
		ps1.setString(1,","+absoluteFile);
		ps1.setString(2, uniqueFile2);	
		ps1.executeUpdate();		
	}

	public void close() throws SQLException {
		con.close();
		
	}

	/*public Boolean check(String uniqueFile1) throws SQLException
	{
		Boolean sts = null;
		String sql = "SELECT * FROM duplists.dupdb WHERE id=?";
		ps = con.prepareStatement(sql);
		ps.setString(1, uniqueFile1);
		ResultSet rs = ps.executeQuery();
	
		if(rs.isBeforeFirst())
		{
			sts =true;
		}
		else
		{
			sts = false;
		}
		return sts;
	}*/
	
}
