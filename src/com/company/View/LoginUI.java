package com.company.View;
import com.company.Controller.LoginController;
import com.company.Entity.Customer;
import com.company.Entity.Movie;
import com.company.Entity.Staff;
import com.company.Utils.Utils;

import java.io.IOException;
import java.util.Scanner;

import java.util.*;

/**
 *  Login UI to allow user to sign in as admin or movie goer
 */
public class LoginUI {
    /**
     * Display UI for user to choose user type ( Admin / Movie Goer )
     */
   public void displayLoginPage() {
      System.out.println("Select User Type: ");
      System.out.println("1) Admin");
      System.out.println("2) Movie Goer");

      boolean exit = false;
      while(!exit) {
         Scanner sc = new Scanner(System.in);
         try {
            int choice = sc.nextInt();
            switch(choice) {
               case 1:
                  displayAdminLoginPage();
                  exit=true;
                  break;
               case 2:
                  displayLoginRegisterPage();
                  exit=true;
                  break;
               default:
                  System.out.println("Please input 1 or 2.");
            }
         }
         catch (InputMismatchException e) {
            System.out.println("Please input an integer.");
         }
      }
   }

    /**
     * Allow Movie Goer to login or register
     */
   public void displayLoginRegisterPage()
   {
      Utils.displayHeader("Login/Register");
      boolean end= false;

while (!end) {
   System.out.println("Please make a selection");
   System.out.println("1) Log In");
   System.out.println("2) Register");

   int select = Utils.getUserChoice(1,2);
   switch (select) {
      case 1:
         displayPublicLoginPage();
         end = true;
         break;
      case 2:
         displayPublicRegisterPage();
         break;
      default:
         System.out.println("Please select 1 or 2");
   }

}
   }

    /**
     * Allow user goer to  register a new account
     */
   public void displayPublicRegisterPage() {
      Utils.displayHeader("Customer Register");
      LoginController logCtrl = new LoginController();
      ArrayList<Customer> customers = null;

      boolean  credentialCheck = false;

         System.out.println("Please input Email:");
         Scanner sc = new Scanner(System.in);
         String email = sc.nextLine();
         credentialCheck = logCtrl.checkCustomer(email);
         if(credentialCheck) {
            System.out.println("Account already exist!");
            displayLoginRegisterPage();
         }
         else
         {
            try {
               customers= (ArrayList<Customer>) Utils.readObject("customer.txt");
            } catch (IOException e) {
               e.printStackTrace();
            } catch (ClassNotFoundException e) {
               e.printStackTrace();
            }

            Customer newcustomer = new Customer(null,null, email);
            customers.add(newcustomer);
            try {
               Utils.writeObject("customer.txt", customers);
            } catch (IOException e) {
               e.printStackTrace();
               return;
            }
            System.out.println("Successfully Registered!");
         }
      }
      //MovieGoerUI mui = new MovieGoerUI();
      //mui.getHomeView();


    /**
     *  Display Movie Goer Log in Page and allow them to login
     */
   public void displayPublicLoginPage() {
      LoginController logCtrl = new LoginController();
      Utils.displayHeader("Customer Login");
      boolean  credentialCheck = false;
      while(!credentialCheck) {
         System.out.println("Please input Email:");
         Scanner sc = new Scanner(System.in);
         String email = sc.nextLine();
         credentialCheck = logCtrl.checkCustomer(email);
         if(!credentialCheck) {
            System.out.println("Invalid account.");
         }
      }
      Customer c = Utils.getCustomerCookie();
      System.out.println("Your email: " + c.getEmail());
      MovieGoerUI mui = new MovieGoerUI();
      mui.getHomeView();
   }

    /**
     * display admin Login page and allow user to log in
     */
   public void displayAdminLoginPage() {
      LoginController logCtrl = new LoginController(); 
      Utils.displayHeader("Admin Login");				
   	
   	//Check password
      boolean credentialCheck = false;
      while(!credentialCheck) {
         Scanner sc = new Scanner(System.in);
         System.out.println("Please input Username:");
         String username = sc.nextLine();
         System.out.println("Please input Password:");
         String password = sc.nextLine();
         credentialCheck = logCtrl.checkAdmin(username,password);
         if(!credentialCheck) {
            System.out.println("Invalid account.");
         }
      }
      Staff s = Utils.getAdminCookie();
      StaffUI sui = new StaffUI();
      sui.displayMenu();
   }
}
