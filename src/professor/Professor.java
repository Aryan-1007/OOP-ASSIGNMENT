package professor;

import core.User;
import core.CourseManager;
import models.Course;
import student.Student;
import java.util.*;

public class Professor extends User implements CourseManager {
    private static final long serialVersionUID = 1L;
    private List<Course> assignedCourses = new ArrayList<>();

    public Professor(String email, String password) {
        super(email, password);
    }

    public void assignCourse(Course c) {
        this.assignedCourses.add(c);
        c.setProfessor(this);
    }

    @Override
    public void viewCourses() {
        System.out.println("\n--- My Assigned Courses ---");
        if(assignedCourses.isEmpty()) System.out.println("No courses assigned yet.");
        for (Course c : assignedCourses) {
            c.displayDetails();
            System.out.println("-");
        }
    }

    public void updateCourseDetails() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Course Code to update: ");
        String code = sc.next();

        Course target = null;
        for (Course c : assignedCourses) {
            if (c.getCourseCode().equalsIgnoreCase(code)) target = c;
        }

        if (target == null) { System.out.println("You do not teach this course."); return; }

        System.out.println("1. Update Syllabus\n2. Update Timings\n3. Update Enrollment Limit\n4. Update Office Hours");
        System.out.print("Choose option: ");
        int opt = sc.nextInt();
        sc.nextLine(); // consume newline

        if (opt == 1) { System.out.print("New Syllabus: "); target.setSyllabus(sc.nextLine()); }
        if (opt == 2) { System.out.print("New Timings: "); target.setTimings(sc.nextLine()); }
        if (opt == 3) { System.out.print("New Limit: "); target.setEnrollmentLimit(sc.nextInt()); }
        if (opt == 4) { System.out.print("New Office Hours: "); target.setOfficeHours(sc.nextLine()); }
        System.out.println("Course updated successfully.");
    }

    public void viewEnrolledStudents() {
        if(assignedCourses.isEmpty()) System.out.println("No courses assigned yet.");
        for (Course c : assignedCourses) {
            System.out.println("\nStudents in " + c.getCourseCode() + ":");
            if(c.getEnrolledStudents().isEmpty()) System.out.println("No students enrolled.");
            for (Student s : c.getEnrolledStudents()) {
                System.out.println("- " + s.getEmail() + " | Completed Courses: " + s.getCompletedCourses().size());
            }
        }
    }

    @Override
    public void showDashboard() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Professor Menu ===");
            System.out.println("1. View My Courses");
            System.out.println("2. Update Course Details");
            System.out.println("3. View Enrolled Students");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            if (choice == 1) viewCourses();
            else if (choice == 2) updateCourseDetails();
            else if (choice == 3) viewEnrolledStudents();
            else if (choice == 4) { logout(); break; }
        }
    }
}