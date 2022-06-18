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

import java.sql.SQLException;

import spark.ModelAndView;

public class DBRoutes {

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
        try{
            UserDAO dao = Postgres.getUserDAO();
            dao.addUser(req.queryParams("username"), req.queryParams("password"), req.queryParams("full_name"));
        } catch(SQLException sx) {
            sx.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        model.put("name", req.queryParams("username"));
        return new HandlebarsTemplateEngine()
            .render(new ModelAndView(model, "userAdded.hbs"));
    }

    public String mainPage(Request req, Response res) {
	Map<String, Object> model = new HashMap<String, Object>();
	return new HandlebarsTemplateEngine()
	    .render(new ModelAndView(model, "main.hbs"));
    }
    
    public String login(Request req, Response res) {
	Map<String, Object> model = new HashMap<String, Object>();
	return new HandlebarsTemplateEngine()
	    .render(new ModelAndView(model, "login.hbs"));
    }

    public String loginPost(Request req, Response res) {
	try {
	    String username = req.queryParams("username");
	    UserDAO dao = Postgres.getUserDAO();
	    User u = dao.getUser(username);
	    if (u == null || !u.getPassword().equals(req.queryParams("password"))) {
		System.out.println("Invalid username or password: " +u);
		res.redirect("/login");
		return "";
	    }
	    req.session().attribute("user", username);
	    res.redirect("/admin");
	} catch (Exception ex) {
	    return "Error: " + ex.getMessage();
	}
	return "";
    }

    public void addRoutes() {
	get("/admin", (req, res) -> admin(req, res));
	get("/admin/updateUser/:username", (req, res) -> updateUser(req, res));
	get("/admin/updateUser/admin/userUpdated/:username", (req, res) -> userUpdated(req, res));
	get("/admin/deleteUser/:username", (req, res) -> deleteUser(req, res));
	get("/admin/addUser", (req, res) -> addUser(req, res));
	get("/admin/admin/userAdded", (req, res) -> userAdded(req, res));
	get("/login", (req, res) -> login(req,res));
	post("/login", (req, res) -> loginPost(req,res));
	get("/", (req, res) -> mainPage(req, res));
    }
}
