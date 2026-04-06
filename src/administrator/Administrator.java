package administrator;

import core.User;
import core.CourseManager;
import models.Course;
import models.Complaint;
import professor.Professor;
import student.Student;
import utils.Database;
import java.util.*;

public class Administrator extends User implements CourseManager {
    private static final long serialVersionUID = 1L;

    public Administrator(String email, String password) {
        super(email, password);
    }

    @Override
    public void viewCourses() {
        System.out.println("\n--- Course Catalog ---");
        for (Course c : Database.courseCatalog) {
            c.displayDetails();
            System.out.println("-");
        }
    }

    public void addCourse() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Course Code (e.g., CS101): ");
        String code = sc.nextLine();
        System.out.print("Enter Course Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Credits (2 or 4): ");
        int credits = sc.nextInt();
        System.out.print("Enter Enrollment Limit: ");
        int limit = sc.nextInt();
        System.out.print("Enter Semester: ");
        int semester = sc.nextInt();
        sc.nextLine(); // consume newline

        Course newCourse = new Course(code, title, credits, limit, semester);

        System.out.print("Does this course have prerequisites? (y/n): ");
        if (sc.next().equalsIgnoreCase("y")) {
            sc.nextLine(); // consume newline
            System.out.print("Enter prerequisite course codes (comma-separated): ");
            String[] prereqCodes = sc.nextLine().split(",");
            for (String prereqCode : prereqCodes) {
                boolean found = false;
                for (Course c : Database.courseCatalog) {
                    if (c.getCourseCode().equalsIgnoreCase(prereqCode.trim())) {
                        newCourse.addPrerequisite(c);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    System.out.println("Warning: Prerequisite course '" + prereqCode.trim() + "' not found in catalog.");
                }
            }
        }

        Database.courseCatalog.add(newCourse);
        System.out.println("Course Added successfully.");
    }

    public void deleteCourse() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Course Code to delete: ");
        String code = sc.next();
        boolean removed = Database.courseCatalog.removeIf(c -> c.getCourseCode().equalsIgnoreCase(code));
        if (removed) {
            System.out.println("Course deleted successfully.");
        } else {
            System.out.println("Course not found.");
        }
    }
    
    public void assignProfessorToCourse() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Course Code: ");
        String courseCode = sc.nextLine();
        
        Course targetCourse = null;
        for (Course c : Database.courseCatalog) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                targetCourse = c;
                break;
            }
        }
        
        if (targetCourse == null) {
            System.out.println("Course not found in catalog.");
            return;
        }
        
        System.out.print("Enter Professor Email to assign: ");
        String profEmail = sc.nextLine();
        
        Professor targetProf = null;
        for (User u : Database.allUsers) {
            if (u instanceof Professor && u.getEmail().equalsIgnoreCase(profEmail)) {
                targetProf = (Professor) u;
                break;
            }
        }
        
        if (targetProf == null) {
            System.out.println("Professor not found.");
            return;
        }
        
        targetProf.assignCourse(targetCourse);
        System.out.println("Professor " + profEmail + " assigned to course " + courseCode + " successfully.");
    }

    public void manageStudentRecords() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Student Email to mark course complete: ");
        String email = sc.next();

        Student target = null;
        for (core.User u : Database.allUsers) {
            if (u instanceof Student && u.getEmail().equalsIgnoreCase(email)) {
                target = (Student) u;
                break;
            }
        }

        if (target == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Enter Course Code: ");
        String code = sc.next();
        System.out.print("Enter Final Grade (4-10 for pass, 0 for fail): ");
        int grade = sc.nextInt();

        for (Course c : Database.courseCatalog) {
            if (c.getCourseCode().equalsIgnoreCase(code)) {
                target.markCourseCompleted(c, grade);
                System.out.println("Course marked as completed for " + email);
                return;
            }
        }
        System.out.println("Course not found in catalog.");
    }

    public void handleComplaints() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n1. View All Complaints\n2. Filter by 'Pending'\n3. Resolve a Complaint");
        System.out.print("Choose option: ");
        int opt = sc.nextInt();
        sc.nextLine(); // consume newline

        if (opt == 1 || opt == 2) {
            System.out.println("\n--- Complaints List ---");
            boolean found = false;
            for (Complaint c : Database.allComplaints) {
                if (opt == 1 || c.getStatus().equalsIgnoreCase("Pending")) {
                    c.displayComplaint();
                    found = true;
                }
            }
            if (!found) System.out.println("No complaints match your criteria.");
        } else if (opt == 3) {
            System.out.print("Enter Complaint ID to resolve: ");
            int id = sc.nextInt();
            sc.nextLine(); // consume newline
            System.out.print("Enter Resolution Details: ");
            String details = sc.nextLine();

            for (Complaint c : Database.allComplaints) {
                if (c.getComplaintID() == id) {
                    c.resolveComplaint(details);
                    System.out.println("Complaint resolved successfully.");
                    return;
                }
            }
            System.out.println("Complaint ID not found.");
        }
    }

    public void viewAllUsers() {
        System.out.println("\n--- User Directory ---");
        if (Database.allUsers.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        for (User u : Database.allUsers) {
            String role = u.getClass().getSimpleName();
            System.out.println("[" + role + "] " + u.getEmail());
        }
    }

    public void deleteUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter User Email to delete: ");
        String email = sc.nextLine();
        
        // Prevent admin from deleting themselves
        if (this.getEmail().equalsIgnoreCase(email)) {
            System.out.println("You cannot delete your own account.");
            return;
        }

        // Find the user first
        User userToDelete = null;
        for (User u : Database.allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                userToDelete = u;
                break;
            }
        }

        if (userToDelete == null) {
            System.out.println("User not found.");
            return;
        }

        // If the user is a student, un-enroll them from all courses
        if (userToDelete instanceof Student) {
            Student student = (Student) userToDelete;
            for (Course c : Database.courseCatalog) {
                c.removeStudent(student);
            }
        }

        // Now, delete the user from the main user list
        boolean removed = Database.deleteUser(email);
        if (removed) {
            Database.saveData(); // Save changes to the database file immediately
            System.out.println("User deleted successfully and unenrolled from all courses.");
        } else {
            System.out.println("User not found.");
        }
    }

    @Override
    public void showDashboard() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Administrator Menu ===");
            System.out.println("1. View Catalog");
            System.out.println("2. Add Course");
            System.out.println("3. Delete Course");
            System.out.println("4. Assign Professor to Course");
            System.out.println("5. Mark Course as Completed for Student");
            System.out.println("6. Handle Complaints (Filter & Resolve)");
            System.out.println("7. View All Users");
            System.out.println("8. Delete User");
            System.out.println("9. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            if (choice == 1) viewCourses();
            else if (choice == 2) addCourse();
            else if (choice == 3) deleteCourse();
            else if (choice == 4) assignProfessorToCourse();
            else if (choice == 5) manageStudentRecords();
            else if (choice == 6) handleComplaints();
            else if (choice == 7) viewAllUsers();
            else if (choice == 8) deleteUser();
            else if (choice == 9) { logout(); break; }
            else System.out.println("Invalid choice.");
        }
    }
}