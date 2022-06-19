package edu.au.cc.gallery.data;


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
	ResultSet rs = connection.execute("select username, password, full_name, admin from users");
	while(rs.next()) {
	    result.add(new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
	    //username, password, full_name
	}
	connection.close();
	return result;
    }

    public User getUser(String username) throws SQLException {
	User result;
	ResultSet rs = connection.executeRS("select password, full_name, admin from users where username=?", new String[] {username});
	
	//	rs.next();
	if(rs.next()) {
	    result=new User(username, rs.getString(1), rs.getString(2), rs.getString(3));
	    rs.close();
	    return result;
	}
	rs.close();
	return null;
    }

    public void updateUser(String username, String password, String full_name) throws SQLException {
	connection.execute("update users set password=?, full_name=? where username=?", new String[] {password, full_name, username});
    }

    public void deleteUser(String username) throws SQLException {
        connection.execute("delete from users where username=?", new String[] {username});
    }

    public void addUser(String username, String password, String full_name, String admin) throws SQLException{
	connection.execute("insert into users (username, password, full_name, admin) values (?, ?, ?, ?)", new String[] {username, password, full_name, admin});
    }
}
