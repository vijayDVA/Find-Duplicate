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
	public static PreparedStatement ps;
	
    static{ try {
		Class.forName(dbDriver);
		con = DriverManager.getConnection(dbUrl, dbUname, dbPassword);
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}}	
	

	public void add(String locations, String absolutePath, String uniqueFile1) throws SQLException
	{
		
		
		String sql2 = "INSERT INTO duplists.dupdb (id,File_locations) VALUES(?,?)";
		ps = con.prepareStatement(sql2);
		ps.setString(1,uniqueFile1);
		ps.setString(2, locations+','+absolutePath);
		ps.executeUpdate();		
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


	public void update(String uniqueFile2, File absoluteFile) throws SQLException
	{
	
		String sql1 = "UPDATE duplists.dupdb dl SET dl.File_locations = concat(dl.File_locations,?) where dl.id=?";
		
		ps = con.prepareStatement(sql1);
		ps.setString(1,","+absoluteFile);
		ps.setString(2, uniqueFile2);	
		ps.executeUpdate();
		
	}

	public void close() throws SQLException {
		con.close();
		
	}

	
	
}
