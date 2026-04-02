package student;

import models.Course;
import core.User;
import utils.Database;
import java.util.Scanner;

// Inherits from Student, gaining all student functionalities plus extra grading assistance
public class TeachingAssistant extends Student {
    private static final long serialVersionUID = 1L;
    private Course assignedCourse;

    public TeachingAssistant(String email, String password, Course assignedCourse) {
        super(email, password);
        this.assignedCourse = assignedCourse;
    }

    public void manageGrades() {
        if (assignedCourse == null) {
            System.out.println("You are not assigned as a TA to any course.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- TA Grade Management for " + assignedCourse.getCourseCode() + " ---");
        if (assignedCourse.getEnrolledStudents().isEmpty()) {
            System.out.println("No students enrolled in this course.");
            return;
        }

        System.out.println("Enrolled Students:");
        for (Student s : assignedCourse.getEnrolledStudents()) {
            System.out.println("- " + s.getEmail());
        }

        System.out.print("Enter Student Email to assign grade: ");
        String email = sc.next();
        System.out.print("Enter Grade (A/B/C): ");
        String grade = sc.next();

        for (Student s : assignedCourse.getEnrolledStudents()) {
            if (s.getEmail().equalsIgnoreCase(email)) {
                s.assignGrade(assignedCourse, grade);
                System.out.println("Grade updated successfully for " + email);
                return;
            }
        }
        System.out.println("Student not found in this course.");
    }

    @Override
    public void showDashboard() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Teaching Assistant Menu ===");
            System.out.println("1. Access Student Menu (Register, Drop, View Schedule, etc.)");
            System.out.println("2. Manage Student Grades (TA Feature)");
            System.out.println("3. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                super.showDashboard(); // Uses inherited Student menu
            } else if (choice == 2) {
                manageGrades();
            } else if (choice == 3) {
                logout(); break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}