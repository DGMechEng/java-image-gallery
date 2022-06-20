package edu.au.cc.gallery.data;


import java.util.List;

import edu.au.cc.gallery.data.image.Link;

public interface UserDAO {
    List<User> getUsers() throws Exception;

    User getUser(String username) throws Exception;

    void updateUser(String username, String password, String full_name) throws Exception;

    void deleteUser(String username) throws Exception;

    void addUser(String username, String password, String full_name, String admin) throws Exception;

    List<Link> getImageLinks(String username) throws Exception;
}
