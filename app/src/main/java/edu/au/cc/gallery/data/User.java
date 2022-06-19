package edu.au.cc.gallery.data;


public class User {
    private String username;
    private String password;
    private String fullName;
    private String admin;

    public User(String username, String password, String fullName, String admin) {
	this.username = username;
	this.password = password;
	this.fullName = fullName;
	this.admin = admin;
    }
    public String getUsername() { return username; }
    public void setUsername(String u) { username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { password = p; }
    public String getFullName() { return fullName; }
    public void setFullName(String f) { fullName = f; }
    public String getAdmin() { return admin; }
    public void setAdmin(String a) { admin = a; } 
}
