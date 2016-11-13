/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication;

import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The Authentication class provices module Authentication System
 */
public class Authentication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        mainMenu();        
    }

    private ArrayList<Authentication.Credential> credentials; // list object Credential
    private String username; // username log in to System
    private String password; // password log in to System
    private String role; // role of user log in to System

    /**
     * Class constructor and run flow of module.
     * @param fileName the name of file stored all credentials.
     */
    public Authentication(String fileName) {
        credentials = new ArrayList<Authentication.Credential>();
        username = "";
        password = "";
        setCredentials(getFile(fileName));
        loginScreen();
        int limitAuthenFail = 2;
        for (int i = 0; i < limitAuthenFail; i++) {   
            if (authenticate()) {
                break;
            } else {
                System.out.println("Incorrect information. Please try again!");
                loginScreen();
                if (i == (limitAuthenFail - 1)) {
                    System.out.println("You're wrong too many times allowed. Press to exit !");
                    try {
                        System.in.read();
                        System.exit(0);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        getDisplayRole(getFile("resources/" + role+".txt"));
        logout();
    }
    
    /**
     * This method display main menu of module.
     */
    
    private static void mainMenu() {
        String selection;
        Scanner input = new Scanner(System.in);

        // Display menu graphics
        System.out.println("============================");
        System.out.println("|WELCOME TO SYSTEM THE ZOO |");
        System.out.println("============================");
        System.out.println("| Options:                 |");
        System.out.println("|        1. Log in         |");
        System.out.println("|        2. Exit           |");
        System.out.println("============================");
        System.out.print(" Select option: ");
        selection = input.nextLine();

        // Switch construct
        switch (selection) {
            case "1":
                new Authentication("resources/credentials.txt");
                break;
            case "2":
                exit(0);
            default:
                mainMenu();
                break; // This break is not really necessary
        }
    }
    /**
     * This method display login and options select action.
     */
    private void loginScreen() {
        System.out.print("| Username: ");
        Scanner input = new Scanner(System.in);
        username = input.nextLine();
        System.out.print("| Password: ");
        password = input.nextLine();
    }

    /**
     * Return true or false.
     * <p>
     * This method always returns immediately, whether or not the username and password had been stored.
     * @return true/false
     */
    private boolean authenticate() {
        for (Authentication.Credential credential : credentials) {
            if (username.equals(credential.username)) {
                if (encodeMd5(password).equals(credential.passwordMd5)) {
                    role = credential.role;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return an File object that can be find in package resources.
     * The file name argument must specify an absolute path file.
     * <p>
     * This method always return an File object.
     * @param fileName the name of file want to get.
     * @return the File object
     */
    private File getFile(String fileName) {
        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        return file;
    }

    /**
     * This method read file "credentials.txt" and add credential object to a list.
     * @param file the File object "credentials.txt"
     */
    private void setCredentials(File file) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] credentialData = line.split("\t");
                credentials.add(new Authentication.Credential(credentialData[0],
                        credentialData[1], credentialData[2], credentialData[3]));
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns a string that can then be encode MD5.
     * <p>
     * This method create an MD5 hash of a String and always returns immediately.
     * @param password the string will be encode MD5.
     * @return the password was encode MD5
     */
    private String encodeMd5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return "";
        }

    }
    
    /**
     * This method display content in the role file after successful authentication. 
     * @param file the File object role of user log in to System
     */
    private void getDisplayRole(File file) {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                System.out.println(scanner.nextLine());                
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * This method display logout screen.
     */
    private void logout() {
        System.out.print("| Enter 0 to log out: ");
        Scanner input = new Scanner(System.in);
        switch(input.nextLine()) {
            case "0":
                mainMenu();
                break;
            default:
                logout();
        }
    }

    /**
     * Credential is the inner class for an instance of object credential.
     */
    private static class Credential {

        private String username;
        private String passwordMd5;
        private String password;
        private String role;

        public Credential(String username, String passwordMd5, String password, String role) {
            this.username = username;
            this.passwordMd5 = passwordMd5;
            this.password = password;
            this.role = role;
        }
    }
}
