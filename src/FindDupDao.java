import java.sql.Connection;

import java.util.Map;
import java.util.Set;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FindDupDao {

	private String dbUrl = "jdbc:mysql://localhost:3306/duplists";
	private String dbUname = "root";
	private String dbPassword = "1234";
	private String dbDriver = "com.mysql.cj.jdbc.Driver";
	
	public void loadDriver(String dbDriver)
	{
		try {
			Class.forName(dbDriver);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection()
	{
		Connection con = null;
		try {
			con = DriverManager.getConnection(dbUrl, dbUname, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public void insert(Map<String, Set<String>> finalMap) throws SQLException
	{
		
		loadDriver(dbDriver);
		Connection con = getConnection();
		String sql = "insert into dupdb(File_locations) values(?)";
		
		PreparedStatement ps;
		
		if(con != null) {			
		
		ps = con.prepareStatement(sql);
		for (Set<String> list : finalMap.values()) {
            if (list.size() > 1) {
            	
            	String location =list.toString();
            	ps.setString(1, location);
            	ps.executeUpdate();
                //System.out.println("\n");
            	
                //for (String file : list) {
                //    System.out.println(file);
                   
                //}
            }
        }
		
		
		con.close();
			
		}
	}
	
}
