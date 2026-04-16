package student;

import models.Course;
import utils.Database;

import java.io.Serial;
import java.util.Scanner;

public class TeachingAssistant extends Student {
    @Serial
    private static final long serialVersionUID = 1L;
    private Course assignedCourse;

    public TeachingAssistant(String email, String password) {
        super(email, password);
        this.assignedCourse = null;
    }

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

    public void assignGrade(String studentEmail, int grade) {
        if (assignedCourse == null) {
            throw new IllegalStateException("You are not assigned to a course.");
        }

        Student targetStudent = null;
        for (Student s : assignedCourse.getEnrolledStudents()) {
            if (s.getEmail().equalsIgnoreCase(studentEmail)) {
                targetStudent = s;
                break;
            }
        }

        if (targetStudent == null) {
            throw new IllegalArgumentException("Student not found or not enrolled in this course.");
        }

        targetStudent.assignGrade(assignedCourse, grade);
        Database.saveData();
    }

    public String getEnrolledStudentsAsString() {
        if (assignedCourse == null) {
            return "You are not assigned to a course.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\nStudents in ").append(assignedCourse.getCourseCode()).append(":\n");
        if(assignedCourse.getEnrolledStudents().isEmpty()) {
            sb.append("No students enrolled.\n");
        } else {
            for (Student s : assignedCourse.getEnrolledStudents()) {
                sb.append("- ").append(s.getEmail()).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public void showDashboard() {
        // Console implementation
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

            try {
                if (choice == 1) {
                    System.out.println(getEnrolledStudentsAsString());
                } else if (choice == 2) {
                    System.out.print("Enter Student Email: ");
                    String email = sc.next();
                    System.out.print("Enter Grade (4-10 for pass, 0 for fail): ");
                    int grade = sc.nextInt();
                    assignGrade(email, grade);
                } else if (choice == 3) {
                    logout();
                    break;
                } else {
                    System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }
}