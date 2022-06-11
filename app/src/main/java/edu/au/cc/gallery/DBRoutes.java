package edu.au.cc.gallery;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import java.sql.SQLException;

import spark.ModelAndView;

public class DBRoutes {

    public String admin(Request req, Response res) {
	Map<String, Object> model = new HashMap<String, Object>();
	try{
	    UserDAO dao = Postgres.getUserDAO();
	    for(User u: dao.getUsers()) {
		model.put("username", u.getUsername());
		//		System.out.println("Hey, look at me: " + u.getUsername());
	    }
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
	    //not compiling, not recognizing u as a User
	    System.out.println("Got user: "+ u.getUserName());
	} catch(SQLException sx) {
	    sx.printStackTrace();
	} catch(Exception e) {
	    e.printStackTrace();
	}
	model.put("username", u.getUserName());
	model.put("password", u.getPassword());
	model.put("full_name", u.getFullName());
	return new HandlebarsTemplateEngine()
	    .render(new ModelAndView(model, "updateUser.hbs"));
    }
    
    public void addRoutes() {
	get("/admin", (req, res) -> admin(req, res));
	get("/admin/updateUser/:username", (req, res) -> updateUser(req, res));
    }


}
