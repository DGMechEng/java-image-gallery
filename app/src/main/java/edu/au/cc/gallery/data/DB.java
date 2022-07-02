package edu.au.cc.gallery.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import edu.au.cc.gallery.aws.S3;
import edu.au.cc.gallery.aws.Secrets;

import org.json.JSONArray;
import org.json.JSONObject;


public class DB {

    private String dbUrl=System.getenv("PG_HOST");
    private Connection connection;

    private String getPassword() {
	try {
	File f = new File(system.getenv("IG_PASSWD_FILE"));
	Scanner s = new Scanner(myObj);
	if(s.hasNextLine()) {
	    return s.nextLine();
	}
	} catch(FileNotFoundException fnx) {
	    System.out.println("Not able to get db password");
	    fnx.printStackTrace();
	}
    }
    
    public void connect() throws SQLException {
	try {
	    Class.forName("org.postgresql.Driver");
	    
	    connection = DriverManager.getConnection(getDBUrl, getenv("IG_DATABASE"), getPassword());
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
