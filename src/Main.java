import core.User;
import student.Student;
import student.TeachingAssistant;
import professor.Professor;
import administrator.Administrator;
import models.Course;
import utils.Database;
import exceptions.InvalidLoginException;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        Database.loadData();

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
                    Database.saveData();
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
                sc.nextLine();
            } catch (InvalidLoginException e) {
                System.out.println("\n[ERROR]: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                sc.nextLine();
            }
        }
    }

    private static void handleSignUp(Scanner sc) {
        System.out.println("\nSign Up As:");
        System.out.println("1. Student");
        System.out.println("2. Professor");
        System.out.println("(Note: Administrators and TAs cannot sign up here)");
        System.out.print("Choose role: ");

        int roleChoice = -1;
        try {
            roleChoice = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter 1 or 2.");
            sc.nextLine();
            return;
        }

        if (roleChoice != 1 && roleChoice != 2) {
            System.out.println("Invalid role selection.");
            return;
        }

        System.out.print("Enter new Email: ");
        String email = sc.next();

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
        Database.saveData();
    }

    // Handles InvalidLoginException as per Assignment 2
    private static void handleLogin(Scanner sc) throws InvalidLoginException {
        System.out.println("\nLogin As:");
        System.out.println("1. Student");
        System.out.println("2. Professor");
        System.out.println("3. Administrator");
        System.out.println("4. Teaching Assistant");
        System.out.print("Choose role: ");

        int roleChoice = -1;
        try {
            roleChoice = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter 1, 2, 3, or 4.");
            sc.nextLine();
            return;
        }

        System.out.print("Email: ");
        String email = sc.next();
        System.out.print("Password: ");
        String pass = sc.next();

        User loggedInUser = null;
        for (User u : Database.allUsers) {
            if (u.login(email, pass)) {
                // Ensure proper instance checking including the new TA role
                if ((roleChoice == 1 && u instanceof Student && !(u instanceof TeachingAssistant)) ||
                        (roleChoice == 2 && u instanceof Professor) ||
                        (roleChoice == 3 && u instanceof Administrator) ||
                        (roleChoice == 4 && u instanceof TeachingAssistant)) {
                    loggedInUser = u;
                    break;
                }
            }
        }

        if (loggedInUser == null) {
            throw new InvalidLoginException("Incorrect email, password, or role selection.");
        }

        System.out.println("Login Successful!");
        try {
            loggedInUser.showDashboard();
        } catch (Exception e) {
            System.out.println("An error occurred in dashboard: " + e.getMessage());
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

        c2.addPrerequisite(c1);
        p1.assignCourse(c1);
        p1.assignCourse(c2);

        Database.courseCatalog.add(c1);
        Database.courseCatalog.add(c2);

        // Pre-create a TA assigned to CS101 for immediate testing
        TeachingAssistant ta1 = new TeachingAssistant("ta@univ.edu", "pass", c1);
        Database.allUsers.add(ta1);
    }
}