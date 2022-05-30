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

    //return result set based on query
    public ResultSet executeRS(String query, String[] values) throws SQLException {
	PreparedStatement stmt = connection.prepareStatement(query);
	for (int i = 0; i < values.length; i++) {
	    stmt.setString(i+1, values[i]);
	}
	ResultSet rs = stmt.executeQuery();
	return rs;
    }

    public ResultSet execute(String query) throws SQLException{
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

    public static boolean exists(String username) throws Exception {
	DB db = new DB();
	db.connect();
	ResultSet rs;
	rs = db.executeRS("select username, full_name from users where username = ?", new String[] {username});
	//check to see if resultset is empty
	db.close();
	
	if(rs.next()) {
	    //first, return existing records (for information only)
	    return true;
	}
	//if empty, throw exception back to calling routine                                                                                                            
	else {
	    return false;
	}
    }
    
    public static void demo(int operation, String username, String password, String full_name) throws Exception {
	DB db = new DB();
	db.connect();

	ResultSet rs;
	
	switch (operation) {
	    //operation 1: show all users
	case 1:
	    rs = db.execute("select username, password, full_name from users");
	    while (rs.next()) {
		System.out.println(rs.getString(1)+", " + rs.getString(2)+", "+rs.getString(3));
	    }
	    break;
	    //operation 2: add user specified by user input
	case 2:
	    try {
	    db.execute("insert into users (username, password, full_name) values (?, ?, ?)", new String[] {username, password, full_name});
	    } catch (SQLException px) {
		System.out.println("Error: username already exists");
	    }
	    break;
	case 3:
	    //try {
	    //update tuple
     	    if(password.length()==0 && full_name.length() != 0) {
		db.execute("update users set full_name=? where username=?", new String[] {full_name, username});
	    }
	    else if (password.length() != 0 && full_name.length() == 0) {
		db.execute("update users set password=? where username=?", new String[] {password, username});
	    }
	    else if (password.length() == 0 && full_name.length() == 0) {
		System.out.println("No updates were made");
	    }
	    else {
		//
		//
		//this has a syntax error somehow
		//
		//
		db.execute("udpate users set password=?, full_name=? where username=?", new String[] {password, full_name, username});
	    }
	    break;
	case 4:
	    db.execute("delete from users where username=?", new String[] {username});
	    System.out.println("User " + username + " deleted");
	    break;
	case 5:
	    db.close();
	default: db.close();
	}
    }
}
