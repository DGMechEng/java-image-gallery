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

    private static final String dbUrl = "jdbc:postgresql://m4-db.c3n5o7gpctqm.us-west-2.rds.amazonaws.com/";
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
}
