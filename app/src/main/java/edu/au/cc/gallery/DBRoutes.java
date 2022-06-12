package edu.au.cc.gallery;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import spark.Request;
import spark.Response;
import static spark.Spark.*;

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
		model.put("user", u.getUsername());
		System.out.println("Added user " + u.getUsername() + " to username map");
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
	    model.put("username",u.getUsername());
	    model.put("password",u.getPassword());
	    model.put("fullName",u.getFullName());
    
	    System.out.println("looking for user " + req.params(":username"));
	    System.out.println("with full name " + u.getFullName());
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

    
    public void addRoutes() {
	get("/admin", (req, res) -> admin(req, res));
	get("/admin/updateUser/:username", (req, res) -> updateUser(req, res));
	get("/admin/updateUser/admin/userUpdated/:username", (req, res) -> userUpdated(req, res));
    }


}
