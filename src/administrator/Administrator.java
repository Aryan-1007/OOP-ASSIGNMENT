package administrator;

import core.User;
import core.CourseManager;
import models.Course;
import models.Complaint;
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

        Database.courseCatalog.add(new Course(code, title, credits, limit));
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

    public void manageStudentRecords() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Student Email to assign grade: ");
        String email = sc.next();

        Student target = null;
        for (core.User u : Database.allUsers) {
            if (u instanceof Student && u.getEmail().equalsIgnoreCase(email)) {
                target = (Student) u; break;
            }
        }

        if (target == null) { System.out.println("Student not found."); return; }

        System.out.print("Enter Course Code: ");
        String code = sc.next();
        System.out.print("Enter Grade (A/B/C): ");
        String grade = sc.next();

        for (Course c : Database.courseCatalog) {
            if (c.getCourseCode().equalsIgnoreCase(code)) {
                target.assignGrade(c, grade);
                System.out.println("Grade assigned. Semester data updated for " + email);
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

    @Override
    public void showDashboard() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Administrator Menu ===");
            System.out.println("1. View Catalog");
            System.out.println("2. Add Course");
            System.out.println("3. Delete Course");
            System.out.println("4. Manage Student Records (Assign Grades)");
            System.out.println("5. Handle Complaints (Filter & Resolve)");
            System.out.println("6. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            if (choice == 1) viewCourses();
            else if (choice == 2) addCourse();
            else if (choice == 3) deleteCourse();
            else if (choice == 4) manageStudentRecords();
            else if (choice == 5) handleComplaints();
            else if (choice == 6) { logout(); break; }
        }
    }
}