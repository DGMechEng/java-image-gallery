package edu.au.cc.gallery;

import java.lang.System;
import java.lang.Exception;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.SQLException;


public class UserAdmin {
    public static void prompt() {

	Scanner in = new Scanner(System.in);
	String username;
	String password;
	String full_name;
	int a = 0;
	
	while (a != 5){
	       System.out.println("Select a database operation:\n"                                                            
                     + "1) List users\n" + "2) Add user\n"                                                                           
                     + "3) Edit user\n" + "4) Delete user\n"                                                                         
			   + "5) Quit");
			
	       try {
		   while (a < 1 || a > 5) {
		       System.out.print("Enter command> ");
		       a = in.nextInt();
		       //		       in.nextLine();
		   }
	       } catch (InputMismatchException ix) {
		   System.out.println("Enter integer values only");
	       }
	
	       switch (a){
	       case 1:
		   try {
		       DB.demo(a, null, null, null);
		   } catch (Exception ex) {
		       ex.printStackTrace();
		   }
		   a=0;
		   break;
		   
	       case 2:
		   try {
		       System.out.print("Username> ");
		       username = in.nextLine();
		       System.out.println();
		       System.out.print("Password> ");
		       password = in.nextLine();
		       System.out.println();
		       System.out.print("Full name> ");
		       full_name = in.nextLine();
		       System.out.println();
		       DB.demo(a, username, password, full_name);
		   } catch (Exception ex) {
		       ex.printStackTrace();
		   }
		   a=0;
		   break;
		   
	       case 3:
		   try {
		       System.out.print("Username to edit> ");
		       in.nextLine();
		       username = in.nextLine();
		       if(DB.exists(username)) {
			   System.out.print("New password (press enter to keep current)> ");
			   password = in.nextLine();
			   if(password.length()==0)
			      System.out.println("The password you entered was :" + password);
			   System.out.print("New full name (press enter to keep current)> ");
			   full_name = in.nextLine();
			   DB.demo(a, username, password, full_name);
		       }
		       else {
			   System.out.println("Username not found");
		       }
		   }
		   catch (Exception ex) {
		       ex.printStackTrace();
		   }
		   a=0;
		   break;
	       case 4:
		   try {
		       System.out.print("Username to delete> ");
		       in.nextLine();
		       username = in.nextLine();
		       if(DB.exists(username)) {
			   DB.demo(a, username, null, null);
		       }
		       else {
			   System.out.println("User " + username + " not found in database");
		       }
		   } catch (Exception ex) {
		       ex.printStackTrace();
		   }
		   a=0;
		   break;
	       case 5:
		   try {
		       DB.demo(a, null, null, null);
		       System.out.prinln("Shutting down");
		   } catch(Exception ex) {
		       ex.printStackTrace();
		   }
		   break;
	       }
	       password = null;
	       username = null;
	       full_name = null;
	}
    }

}
