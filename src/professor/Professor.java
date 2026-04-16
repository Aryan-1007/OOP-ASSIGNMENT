package professor;

import core.User;
import core.CourseManager;
import models.Course;
import models.Feedback;
import student.Student;
import student.TeachingAssistant;
import utils.Database;
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

    public List<Course> getAssignedCourses() {
        return assignedCourses;
    }

    @Override
    public void viewCourses() {
        System.out.println(getMyCoursesAsString());
    }

    public String getMyCoursesAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- My Assigned Courses ---\n");
        if(assignedCourses.isEmpty()) {
            sb.append("No courses assigned yet.\n");
        } else {
            for (Course c : assignedCourses) {
                sb.append(c.getCourseDetailsAsString()).append("\n-\n");
            }
        }
        return sb.toString();
    }

    public void updateCourseDetails(String courseCode, int opt, String newValue) {
        Course target = null;
        for (Course c : assignedCourses) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                target = c;
                break;
            }
        }

        if (target == null) {
            throw new IllegalArgumentException("You do not teach this course or course not found.");
        }

        switch (opt) {
            case 1: target.setSyllabus(newValue); break;
            case 2: target.setTimings(newValue); break;
            case 3: target.setEnrollmentLimit(Integer.parseInt(newValue)); break;
            case 4: target.setOfficeHours(newValue); break;
            default: throw new IllegalArgumentException("Invalid update option.");
        }
        Database.saveData();
    }

    public String getEnrolledStudentsAsString() {
        StringBuilder sb = new StringBuilder();
        if(assignedCourses.isEmpty()) {
            sb.append("No courses assigned yet.\n");
        } else {
            for (Course c : assignedCourses) {
                sb.append("\nStudents in ").append(c.getCourseCode()).append(":\n");
                if(c.getEnrolledStudents().isEmpty()) {
                    sb.append("No students enrolled.\n");
                } else {
                    for (Student s : c.getEnrolledStudents()) {
                        sb.append("- ").append(s.getEmail()).append(" | Completed Courses: ").append(s.getCompletedCourses().size()).append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }

    public String getCourseFeedbackAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- Course Feedback ---\n");
        for (Course c : assignedCourses) {
            sb.append("Feedback for ").append(c.getCourseCode()).append(":\n");
            if (c.getFeedbacks().isEmpty()) {
                sb.append("  No feedback yet.\n");
            } else {
                for (Feedback<?> f : c.getFeedbacks()) {
                    sb.append("  - ").append(f.getStudent().getEmail()).append(" rated: ").append(f.getFeedbackData().toString()).append("\n");
                }
            }
        }
        return sb.toString();
    }
    
    public void assignGrade(String courseCode, String studentEmail, int grade) {
        Course targetCourse = null;
        for (Course c : assignedCourses) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                targetCourse = c;
                break;
            }
        }

        if (targetCourse == null) {
            throw new IllegalArgumentException("You do not teach this course.");
        }

        Student targetStudent = null;
        for (Student s : targetCourse.getEnrolledStudents()) {
            if (s.getEmail().equalsIgnoreCase(studentEmail)) {
                targetStudent = s;
                break;
            }
        }

        if (targetStudent == null) {
            throw new IllegalArgumentException("Student not enrolled in this course.");
        }

        targetStudent.assignGrade(targetCourse, grade);
        Database.saveData();
    }

    public void assignTaToCourse(String courseCode, String taEmail) {
        Course targetCourse = null;
        for (Course c : assignedCourses) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                targetCourse = c;
                break;
            }
        }

        if (targetCourse == null) {
            throw new IllegalArgumentException("You do not teach this course.");
        }

        TeachingAssistant targetTa = null;
        for (User u : Database.allUsers) {
            if (u instanceof TeachingAssistant && u.getEmail().equalsIgnoreCase(taEmail)) {
                targetTa = (TeachingAssistant) u;
                break;
            }
        }

        if (targetTa == null) {
            throw new IllegalArgumentException("No Teaching Assistant found with that email.");
        }

        targetTa.setAssignedCourse(targetCourse);
        Database.saveData();
    }

    @Override
    public void showDashboard() {
        // Console implementation - kept for compatibility if needed, but GUI uses separate methods
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Professor Menu ===");
            System.out.println("1. View My Courses");
            System.out.println("2. Update Course Details");
            System.out.println("3. View Enrolled Students");
            System.out.println("4. View Course Feedback");
            System.out.println("5. Assign TA to Course");
            System.out.println("6. Assign Grade");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            try {
                if (choice == 1) System.out.println(getMyCoursesAsString());
                else if (choice == 2) {
                    System.out.print("Enter Course Code to update: ");
                    String code = sc.next();
                    System.out.println("1. Update Syllabus\n2. Update Timings\n3. Update Enrollment Limit\n4. Update Office Hours");
                    System.out.print("Choose option: ");
                    int opt = sc.nextInt();
                    sc.nextLine(); // consume newline
                    System.out.print("Enter new value: ");
                    String newValue = sc.nextLine();
                    updateCourseDetails(code, opt, newValue);
                    System.out.println("Course updated successfully.");
                }
                else if (choice == 3) System.out.println(getEnrolledStudentsAsString());
                else if (choice == 4) System.out.println(getCourseFeedbackAsString());
                else if (choice == 5) {
                    System.out.print("Enter Course Code to assign a TA to: ");
                    String courseCode = sc.next();
                    System.out.print("Enter TA's Email: ");
                    String taEmail = sc.next();
                    assignTaToCourse(courseCode, taEmail);
                    System.out.println("TA assigned successfully.");
                }
                else if (choice == 6) {
                    System.out.print("Enter Course Code: ");
                    String courseCode = sc.next();
                    System.out.print("Enter Student's Email: ");
                    String studentEmail = sc.next();
                    System.out.print("Enter Grade (4-10 for pass, 0 for fail): ");
                    int grade = sc.nextInt();
                    assignGrade(courseCode, studentEmail, grade);
                    System.out.println("Grade assigned successfully.");
                }
                else if (choice == 7) { logout(); break; }
            } catch (Exception e) {
                System.out.println("\n[ERROR]: " + e.getMessage());
            }
        }
    }
}