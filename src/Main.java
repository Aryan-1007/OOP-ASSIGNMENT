import core.User;
import student.Student;
import professor.Professor;
import administrator.Administrator;
import models.Course;
import utils.Database;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        Database.loadData();

        // Initialize dummy data only if the database is completely empty
        if (Database.allUsers.isEmpty()) {
            initializeData();
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the University Course Registration System");

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Sign Up (New Student/Professor)");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            
            try {
                int initialChoice = sc.nextInt();

                if (initialChoice == 3) {
                    Database.saveData(); // Saves everything to the file before closing
                    System.out.println("Data saved. Exiting System...");
                    break;
                } else if (initialChoice == 2) {
                    handleSignUp(sc);
                } else if (initialChoice == 1) {
                    handleLogin(sc);
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                sc.nextLine(); // Clear the invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                sc.nextLine(); // Clear the invalid input
            }
        }
    }

    // --- New Sign Up Method ---
    private static void handleSignUp(Scanner sc) {
        System.out.println("\nSign Up As:");
        System.out.println("1. Student");
        System.out.println("2. Professor");
        System.out.println("(Note: Administrators cannot sign up here)");
        System.out.print("Choose role: ");
        
        int roleChoice = -1;
        try {
            roleChoice = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter 1 or 2.");
            sc.nextLine(); // Clear the invalid input
            return;
        }

        if (roleChoice != 1 && roleChoice != 2) {
            System.out.println("Invalid role selection.");
            return;
        }

        System.out.print("Enter new Email: ");
        String email = sc.next();

        // Check if user already exists
        for (User u : Database.allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("An account with this email already exists!");
                return;
            }
        }

        System.out.print("Enter new Password: ");
        String pass = sc.next();

        if (roleChoice == 1) {
            Database.allUsers.add(new Student(email, pass));
            System.out.println("Student account created successfully! You can now log in.");
        } else {
            Database.allUsers.add(new Professor(email, pass));
            System.out.println("Professor account created successfully! You can now log in.");
        }
        
        // Save data immediately so credentials are not lost on exception
        Database.saveData();
    }


    // --- Refactored Login Method ---
    private static void handleLogin(Scanner sc) {
        System.out.println("\nLogin As:");
        System.out.println("1. Student");
        System.out.println("2. Professor");
        System.out.println("3. Administrator");
        System.out.print("Choose role: ");
        
        int roleChoice = -1;
        try {
            roleChoice = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter 1, 2, or 3.");
            sc.nextLine(); // Clear the invalid input
            return;
        }

        System.out.print("Email: ");
        String email = sc.next();
        System.out.print("Password: ");
        String pass = sc.next();

        User loggedInUser = null;
        for (User u : Database.allUsers) {
            if (u.login(email, pass)) {
                if ((roleChoice == 1 && u instanceof Student) ||
                        (roleChoice == 2 && u instanceof Professor) ||
                        (roleChoice == 3 && u instanceof Administrator)) {
                    loggedInUser = u;
                    break;
                }
            }
        }

        if (loggedInUser != null) {
            System.out.println("Login Successful!");
            try {
                loggedInUser.showDashboard();
            } catch (Exception e) {
                System.out.println("An error occurred in dashboard: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid credentials or role selection.");
        }
    }

    private static void initializeData() {
        Administrator admin = new Administrator("admin", "admin");
        Database.allUsers.add(admin);

        Professor p1 = new Professor("prof1@univ.edu", "pass");
        Professor p2 = new Professor("prof2@univ.edu", "pass");
        Database.allUsers.add(p1);
        Database.allUsers.add(p2);

        Course c1 = new Course("CS101", "Intro to Programming", 4, 30);
        Course c2 = new Course("CS201", "Advanced Programming", 4, 30);
        Course c3 = new Course("MATH101", "Calculus I", 4, 50);
        Course c4 = new Course("PHY101", "Physics I", 4, 40);
        Course c5 = new Course("ENG101", "English Comp", 2, 20);

        c2.addPrerequisite(c1);
        p1.assignCourse(c1);
        p1.assignCourse(c2);
        p2.assignCourse(c3);

        Database.courseCatalog.add(c1);
        Database.courseCatalog.add(c2);
        Database.courseCatalog.add(c3);
        Database.courseCatalog.add(c4);
        Database.courseCatalog.add(c5);
    }
}