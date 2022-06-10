package edu.au.cc.gallery;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.HashMap;

import spark.ModelAndView;

public class DBRoutes {

    public String admin(Request req, Response res) {
	Map<String, Object> model = new HashMap<String, Object>();
	model.put("name", "Users");
	return new HandlebarsTemplateEngine()
	    .render(new ModelAndView(model, "admin.hbs"));
    }

    public void addRoutes() {
	get("/admin", (req, res) -> admin(req, res));
    }


}
