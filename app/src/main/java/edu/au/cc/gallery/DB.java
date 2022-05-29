package edu.au.cc.gallery;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.Connection;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DB {

    private static final String dbUrl = "jdbc:postgresql://demo-database-1.c3n5o7gpctqm.us-west-2.rds.amazonaws.com/image_gallery";
    private Connection connection;

    private String getPassword() {

	try{
	    BufferedReader br = new BufferedReader(new FileReader("/home/ec2-user/.sql-passwd"));
	    String result = br.readLine();
	    br.close();
	    return result;
	} catch (IOException ex) {
	    System.err.println("Error opening password file.  Make sure .sql-passwd exists and contains a valid password");
	    System.exit(1);
	}
	return null;
    }

    
    public void connect() throws SQLException {
	try{
	    connection = DriverManager.getConnection(dbUrl, "image_gallery", getPassword());
	} catch (SQLException sx) {
	    sx.printStackTrace();
	    System.exit(1);
	}
    }

    public ResultSet execute(String query) throws SQLException {
	PreparedStatement stmt = connection.prepareStatement(query);
	ResultSet rs = stmt.executeQuery();
	return rs;
    }

    public void execute(String query, String[] values) throws SQLException {
	PreparedStatement stmt = connection.prepareStatement(query);
	for (int i = 0; i < values.length; i++) {
	    stmt.setString(i+1, values[i]);
	}
	stmt.execute();
    }
    public void close() throws SQLException {
	connection.close();
    }
    
    public static void demo() throws Exception {
	DB db = new DB();
	db.connect();
	db.execute("update users set password=? where username=?", new String[] {"monkey", "Fred"});
	ResultSet rs = db.execute("select username, password, full_name from users");
	while (rs.next()) {
	    System.out.println(rs.getString(1)+", " + rs.getString(2)+", "+rs.getString(3));
	}
	db.close();
    }
}
