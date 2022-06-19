package edu.au.cc.gallery.data;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import spark.Request;
import spark.Response;
import static spark.Spark.*;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import edu.au.cc.gallery.data.image.*;

import java.sql.SQLException;

import spark.ModelAndView;

public class Admin {

    public String admin(Request req, Response res) {
	Map<String, Object> model = new HashMap<String, Object>();
	try{
	    UserDAO dao = Postgres.getUserDAO();
	    model.put("users", dao.getUsers());
	} catch (SQLException sx) {
	    sx.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	model.put("name", "Users");
	return new HandlebarsTemplateEngine()
	    .render(new ModelAndView(model, "admin.hbs"));
    }

    public String updateUser(Request req, Response res) {
	Map<String, Object> model = new HashMap<String, Object>();
	try{
	    UserDAO dao = Postgres.getUserDAO();
	    User u = dao.getUser(req.params(":username"));
	    model.put("user", u);
	    //	    model.put("user", dao.getUser(req.params(":username")));
	    System.out.println("full name is :" + u.getFullName());
	} catch(SQLException sx) {
	    sx.printStackTrace();
	} catch(Exception e) {
	    e.printStackTrace();
	}
	return new HandlebarsTemplateEngine()
	    .render(new ModelAndView(model, "updateUser.hbs"));
    }

    public String userUpdated(Request req, Response res) {
	Map<String, Object> model = new HashMap<String, Object>();
	try{
	    UserDAO dao = Postgres.getUserDAO();
	    dao.updateUser(req.params(":username"), req.queryParams("password"), req.queryParams("full_name"));
	} catch(SQLException sx) {
            sx.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
	model.put("username", req.params(":username"));
	return new HandlebarsTemplateEngine()
	    .render(new ModelAndView(model, "userUpdated.hbs"));
    }

    public String deleteUser(Request req, Response res) {
	Map<String, Object> model = new HashMap<String, Object>();
        try{
            UserDAO dao = Postgres.getUserDAO();
            dao.deleteUser(req.params(":username"));
	} catch(SQLException sx) {
            sx.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        model.put("name", req.params(":username"));
        return new HandlebarsTemplateEngine()
            .render(new ModelAndView(model, "userDeleted.hbs"));
    }

    public String addUser(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
        return new HandlebarsTemplateEngine()
            .render(new ModelAndView(model, "addUser.hbs"));
    }

    public String userAdded(Request req, Response res) {
        Map<String, Object> model = new HashMap<String, Object>();
	String admin;
        try{
            UserDAO dao = Postgres.getUserDAO();
	    if(req.queryParams("isadmin")=="admin")
		admin="admin";
	    else
		admin="notadmin";
		    
            dao.addUser(req.queryParams("username"), req.queryParams("password"), req.queryParams("full_name"), admin);
        } catch(SQLException sx) {
            sx.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        model.put("name", req.queryParams("username"));
        return new HandlebarsTemplateEngine()
            .render(new ModelAndView(model, "userAdded.hbs"));
    }

    private void checkAdmin(Request req, Response res) {
	System.out.println("User who logged in is: " + req.session().attribute("user"));
	if(!isAdmin(req.session().attribute("user"))) {
	    res.redirect("/login");
	    halt();
	}
    }

    private boolean isAdmin(String username) {
	try {
	    UserDAO dao = Postgres.getUserDAO();
	    User u = dao.getUser(username);
	    System.out.println("The admin value for user " + u + " is " + u.getAdmin());
	    return (u.getAdmin()=="admin");
	} catch (SQLException sx) {
            sx.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	return false;
    }
    
    public void addRoutes() {
	before("/admin/*", (req, res) -> checkAdmin(req, res));
	before("/admin*", (req, res) -> checkAdmin(req, res));
	get("/admin", (req, res) -> admin(req, res));
	get("/admin/updateUser/:username", (req, res) -> updateUser(req, res));
	get("/admin/updateUser/admin/userUpdated/:username", (req, res) -> userUpdated(req, res));
	get("/admin/deleteUser/:username", (req, res) -> deleteUser(req, res));
	get("/admin/addUser", (req, res) -> addUser(req, res));
	get("/admin/admin/userAdded", (req, res) -> userAdded(req, res));
    }
}
