package edu.au.cc.gallery.data.image;

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

import edu.au.cc.gallery.data.*;
import edu.au.cc.gallery.aws.*;

import spark.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;

import javax.servlet.http.*;

public class imageRoutes {


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
	    res.redirect("/");
	} catch (Exception ex) {
	    return "Error: " + ex.getMessage();
	}
	return "";
    }

    public String upload(Request req, Response res) {
	File uploadDir = new File("/home/ec2-user/java-image-gallery/app/src/main/java/edu/au/cc/gallery/upload");
	uploadDir.mkdir();
	//StaticFiles.externalLocation("/home/ec2-user/java-image-gallery/app/src/main/java/edu/au/cc/gallery/upload");
	Path tempFile;	
	try{
	    tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
	    req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
	    InputStream input = req.raw().getPart("uploaded_file").getInputStream();
	    Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
	    logInfo(req, tempFile);
	    System.out.println("uploaded file is: " + tempFile.getFileName());
	    S3Intfc.toS3(tempFile);
	    return "<h1>You uploaded this image:<h1><img src='" + tempFile.getFileName() + "'>";
	} catch (IOException ix) {
	    ix.printStackTrace();
	} catch (ServletException sx) {
	    sx.printStackTrace();
	}
	return "";
    }

    public String getImages(Request req, Response res) {
	return "";
    }
    
    public void addRoutes() {
	get("/login", (req, res) -> login(req,res));
	post("/login", (req, res) -> loginPost(req,res));
	get("/", (req, res) -> mainPage(req, res));
	post("/upload", (req, res) -> upload(req, res));
	get("/view", (req, res) -> getImages(req, res));
    }

    private static void logInfo(Request req, Path tempFile) throws IOException, ServletException {
        System.out.println("Uploaded file '" + getFileName(req.raw().getPart("uploaded_file")) + "' saved as '" + tempFile.toAbsolutePath() + "'");
    }

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
