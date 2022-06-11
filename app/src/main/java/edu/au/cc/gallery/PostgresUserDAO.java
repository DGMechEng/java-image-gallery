package edu.au.cc.gallery;

import java.util.List;
import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresUserDAO implements UserDAO {
    private DB connection;

    public PostgresUserDAO() throws SQLException {
	connection = new DB();
	connection.connect();
    }
    
    public List<User> getUsers() throws SQLException {
	List<User> result = new ArrayList<User>();
	ResultSet rs = connection.execute("select username, password, full_name from users");
	while(rs.next()) {
	    result.add(new User(rs.getString(1), rs.getString(2), rs.getString(3)));
	    //username, password, full_name
	}
	rs.close();
	return result;
    }

    public User getUser(String username) throws SQLException {
	User result;
	ResultSet rs = connection.executeRS("select password, full_name from users where username=?", new String[] {username});
	if(rs.next()) {
	    result=new User(rs.getString(1), rs.getString(2), rs.getString(3));
	}
	rs.close();
	return result;
    }
}
