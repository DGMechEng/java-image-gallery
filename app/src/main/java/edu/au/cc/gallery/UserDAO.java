package edu.au.cc.gallery;

import java.util.List;


public interface UserDAO {
    List<User> getUsers() throws Exception;

    User getUser(String username) throws Exception;

    void updateUser(String username, String password, String full_name) throws Exception;

    void deleteUser(String username) throws Exception;

    void addUser(String username, String password, String full_name) throws Exception;

}
