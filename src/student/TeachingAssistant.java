package student;

import models.Course;
import java.util.Scanner;

public class TeachingAssistant extends Student {
    private static final long serialVersionUID = 1L;
    private Course assignedCourse;

    // Constructor for when a TA is created without an immediate assignment
    public TeachingAssistant(String email, String password) {
        super(email, password);
        this.assignedCourse = null; // Explicitly null
    }

    // Existing constructor for pre-populating data
    public TeachingAssistant(String email, String password, Course assignedCourse) {
        super(email, password);
        this.assignedCourse = assignedCourse;
    }

    public Course getAssignedCourse() {
        return assignedCourse;
    }

    public void setAssignedCourse(Course assignedCourse) {
        this.assignedCourse = assignedCourse;
    }

    public void assignGrade() {
        if (assignedCourse == null) {
            System.out.println("You are not assigned to a course.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("--- Assign Grade for " + assignedCourse.getCourseCode() + " ---");
        System.out.print("Enter Student Email: ");
        String email = sc.nextLine();

        Student targetStudent = null;
        // Find the student only within the enrolled students of the assigned course
        for (Student s : assignedCourse.getEnrolledStudents()) {
            if (s.getEmail().equalsIgnoreCase(email)) {
                targetStudent = s;
                break;
            }
        }

        if (targetStudent == null) {
            System.out.println("Student not found or not enrolled in this course.");
            return;
        }

        System.out.print("Enter Grade (4-10 for pass, 0 for fail): ");
        int grade = sc.nextInt();

        targetStudent.assignGrade(assignedCourse, grade);
        System.out.println("Grade assigned successfully for " + email);
    }
    
    public void viewEnrolledStudents() {
        if (assignedCourse == null) {
            System.out.println("You are not assigned to a course.");
            return;
        }
        System.out.println("\nStudents in " + assignedCourse.getCourseCode() + ":");
        if(assignedCourse.getEnrolledStudents().isEmpty()) {
            System.out.println("No students enrolled.");
        } else {
            for (Student s : assignedCourse.getEnrolledStudents()) {
                System.out.println("- " + s.getEmail());
            }
        }
    }

    @Override
    public void showDashboard() {
        if (assignedCourse == null) {
            System.out.println("\n=== Teaching Assistant Menu ===");
            System.out.println("You are not yet assigned to a course.");
            System.out.println("1. Logout");
            System.out.print("Choose option: ");
            new Scanner(System.in).nextInt();
            logout();
            return;
        }

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Teaching Assistant Menu ===");
            System.out.println("Assigned to: " + assignedCourse.getCourseCode() + " - " + assignedCourse.getTitle());
            System.out.println("1. View Enrolled Students");
            System.out.println("2. Assign Grade");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                viewEnrolledStudents();
            } else if (choice == 2) {
                assignGrade();
            } else if (choice == 3) {
                logout();
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}