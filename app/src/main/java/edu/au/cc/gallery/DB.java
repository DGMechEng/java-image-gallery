package edu.au.cc.gallery;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import org.json.JSONArray;
import org.json.JSONObject;

public class DB {

    private static final String dbUrl = "jdbc:postgresql://ig-database.c3n5o7gpctqm.us-west-2.rds.amazonaws.com/image_gallery";
    private Connection connection;

    private JSONObject getSecret(){
	String s = Secrets.getSecretImageGallery();
	return new JSONObject(s);
    }

    private String getPassword(JSONObject secret) {
	return secret.getString("password");
    }
    
    public void connect() throws SQLException {
	try {
	    Class.forName("org.postgresql.Driver");
	    JSONObject secret = getSecret();
	    connection = DriverManager.getConnection(dbUrl, "image_gallery", getPassword(secret));
	} catch (ClassNotFoundException ex) {
	    ex.printStackTrace();
	    System.exit(1);
	}
    }

    //return result set based on query with input values
    public ResultSet executeRS(String query, String[] values) throws SQLException {
	PreparedStatement stmt = connection.prepareStatement(query);
	for (int i = 0; i < values.length; i++) {
	    stmt.setString(i+1, values[i]);
	}
	ResultSet rs = stmt.executeQuery();
	return rs;
    }

    //return result set based on query with no input values (query is self-contained)
    public ResultSet execute(String query) throws SQLException{
    	PreparedStatement stmt = connection.prepareStatement(query);
    	ResultSet rs = stmt.executeQuery();
    	return rs;
    }

    //No result, execute queries that don't require return of results
    public void execute(String query, String[] values) throws SQLException {
	PreparedStatement stmt = connection.prepareStatement(query);
	for (int i = 0; i < values.length; i++) {
	    stmt.setString(i+1, values[i]);
	}
	stmt.execute();
    }

    //close database connection
    public void close() throws SQLException {
	connection.close();
    }

    //check to see if a specific tuple exists before trying to modify it
    public static boolean exists(String username) throws Exception {
	DB db = new DB();
	db.connect();
	ResultSet rs;
	rs = db.executeRS("select username, full_name from users where username = ?", new String[] {username});
	//check to see if resultset is empty
	db.close();
	
	if(rs.next()) {
	    //if something is returned, then resultset is not empty and tuple was located. REturn true.
	    db.close();
	    return true;
	}
	//if empty, return false
	else {
	    db.close();
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
		db.execute("update users set password=?, full_name=? where username=?", new String[] {password, full_name, username});
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
