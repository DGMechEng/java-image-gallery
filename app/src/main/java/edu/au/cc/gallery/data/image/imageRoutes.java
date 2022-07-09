package edu.au.cc.gallery.data.image;

import static spark.Spark.*;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import spark.Request;
import spark.Response;
import static spark.Spark.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
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
	if (!isLoggedIn(req)){
            res.redirect("/login");
            halt();
	}
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
	if (!isLoggedIn(req)){
	    res.redirect("/login");
	    halt();
	    }
	File uploadDir = new File(System.getenv("UPLOAD_PATH"));
	uploadDir.mkdir();

	Path tempFile;	
	try{
	    String username = req.session().attribute("user");
	    tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
	    req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
	    InputStream input = req.raw().getPart("uploaded_file").getInputStream();
	    Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
	    logInfo(req, tempFile);
	    String uuid = UUID.randomUUID().toString();
	    S3Intfc.toS3(tempFile, uuid);
	    UserDAO dao = Postgres.getUserDAO();
	    dao.addImageUUID(username, uuid);
	    Files.deleteIfExists(tempFile);
	    return "<h1>You uploaded an image</h1>";
	} catch (IOException ix) {
	    ix.printStackTrace();
	} catch (ServletException sx) {
	    sx.printStackTrace();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	return "";
    }

    public String getImages(Request req, Response res) {
	if (!isLoggedIn(req)){
            res.redirect("/login");
            halt();
	}
	Map<String, Object> model = new HashMap<String, Object>();

	//go to database and get uuids

	List<String> uuidlist = new ArrayList<String>();
	String username = req.session().attribute("user");
	try{
	    UserDAO dao = Postgres.getUserDAO();
	    uuidlist = dao.getImageLinks(username);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
	model.put("name", username);
	model.put("imageID", uuidlist);

	return new HandlebarsTemplateEngine()
	  .render(new ModelAndView(model, "images.hbs"));
    }

  public byte[] getImage(Request req, Response res) {
      String uuid = req.params(":uuid");

      byte[] data = new S3Intfc().download(uuid);
      res.status(200);
      res.type("image/jpeg");
      return data;
    }

    public String deleteImage(Request req, Response res) {
        if (!isLoggedIn(req)){
            res.redirect("/login");
            halt();
            }
	try {
	    UserDAO dao = Postgres.getUserDAO();
	    dao.deleteImage(req.params(":uuid"));
	} catch (Exception ex) {
	    return "Error deleting image";
	}
	return "Deleted image";
    }
    
    public void addRoutes() {
	get("/login", (req, res) -> login(req,res));
	post("/login", (req, res) -> loginPost(req,res));
	get("/", (req, res) -> mainPage(req, res));
	post("/upload", (req, res) -> upload(req, res));
	get("/view", (req, res) -> getImages(req, res));
	get("/debugSession", (req, res) -> debugSession(req, res));
	get("/getImage/:uuid", (req, res) -> getImage(req, res));
	get("/deleteImage/:uuid", (req, res) -> deleteImage(req, res));
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
    public boolean isLoggedIn(Request req) {
	if(req.session().attribute("user")==null)
	    return false;
	return true;
    }
    
    private String debugSession(Request req, Response res) {
	StringBuffer sb = new StringBuffer();
	//	for (String key: req.session().attributes()) {
	//  sb.append(key + "->" + req.session().attribute(key)+"<br />");
	//}
	sb.append((String)req.session().attribute("user"));
	return sb.toString();
    }
}
