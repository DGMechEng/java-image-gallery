/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package edu.au.cc.gallery;

import java.util.Map;
import java.util.HashMap;

import static spark.Spark.*;

import spark.ModelAndView;

import spark.template.handlebars.HandlebarsTemplateEngine;

public class App {
    public String getGreeting() {
	  return "App found";
    }

  public static void main(String[] args) throws Exception{
      String portString = System.getenv("JETTY_PORT");
      if (portString == null || portString.equals(""))
	  port(5000);
      else
	  port(Integer.parseInt(portString));
      
      //System.out.println(new App().getGreeting());
      //S3.demo();
      //      UserAdmin.prompt();

      //M3 stuff
      //      port(5000);
            new DBRoutes().addRoutes();
      
  }
}
