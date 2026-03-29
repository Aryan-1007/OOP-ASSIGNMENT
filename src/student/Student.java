package student;

import core.User;
import core.CourseManager;
import models.Course;
import models.Complaint;
import utils.Database;
import java.util.*;

public class Student extends User implements CourseManager {
    private static final long serialVersionUID = 1L;
    private int currentSemester = 1;
    private List<Course> registeredCourses = new ArrayList<>();
    private Map<Course, String> completedCourses = new HashMap<>(); // Maps Course to Grade

    public Student(String email, String password) { super(email, password); }

    public void assignGrade(Course c, String grade) { completedCourses.put(c, grade); }
    public Map<Course, String> getCompletedCourses() { return completedCourses; }

    @Override
    public void viewCourses() {
        System.out.println("\n--- Available Courses ---");
        for (Course c : Database.courseCatalog) {
            c.displayDetails();
            System.out.println("-");
        }
    }

    public void registerForCourse() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Select your current semester to view courses (e.g., 1, 2): ");
        int sem = sc.nextInt();

        System.out.print("Enter Course Code to register (e.g., CS101): ");
        String courseCode = sc.next();

        Course target = null;
        for (Course c : Database.courseCatalog) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) { target = c; break; }
        }

        if (target == null) { System.out.println("Course not found."); return; }
        if (target.getEnrolledStudents().size() >= target.getEnrollmentLimit()) {
            System.out.println("Course is full!"); return;
        }

        int currentCredits = registeredCourses.stream().mapToInt(Course::getCredits).sum();
        if (currentCredits + target.getCredits() > 20) {
            System.out.println("Credit limit exceeded! Maximum is 20."); return;
        }

        for (Course prereq : target.getPrerequisites()) {
            if (!completedCourses.containsKey(prereq)) {
                System.out.println("Prerequisite missing: " + prereq.getCourseCode()); return;
            }
        }

        registeredCourses.add(target);
        target.enrollStudent(this);
        System.out.println("Successfully registered for " + target.getCourseCode());
    }

    public void dropCourse() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Course Code to drop: ");
        String code = sc.next();

        Course toDrop = null;
        for (Course c : registeredCourses) {
            if (c.getCourseCode().equalsIgnoreCase(code)) { toDrop = c; break; }
        }

        if (toDrop != null) {
            registeredCourses.remove(toDrop);
            toDrop.removeStudent(this);
            System.out.println("Successfully dropped " + code);
        } else {
            System.out.println("You are not registered for this course.");
        }
    }

    public void viewSchedule() {
        System.out.println("\n--- Weekly Schedule ---");
        if (registeredCourses.isEmpty()) System.out.println("No courses registered.");
        for (Course c : registeredCourses) {
            System.out.println(c.getCourseCode() + " | Prof: " + (c.getProfessor() != null ? c.getProfessor().getEmail() : "TBA") + " | Time: " + (c.getTimings() != null ? c.getTimings() : "TBA"));
        }
    }

    public void trackAcademicProgress() {
        System.out.println("\n--- Academic Progress ---");
        if (completedCourses.isEmpty()) {
            System.out.println("No completed courses yet. SGPA/CGPA: 0.0");
            return;
        }

        double totalPoints = 0;
        int totalCredits = 0;
        for (Map.Entry<Course, String> entry : completedCourses.entrySet()) {
            System.out.println(entry.getKey().getCourseCode() + ": Grade " + entry.getValue());
            int gradePoint = entry.getValue().equalsIgnoreCase("A") ? 10 : (entry.getValue().equalsIgnoreCase("B") ? 8 : 6);
            totalPoints += (gradePoint * entry.getKey().getCredits());
            totalCredits += entry.getKey().getCredits();
        }
        System.out.println("Current CGPA: " + (totalPoints / totalCredits));
    }

    public void manageComplaints() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n1. Submit a new complaint\n2. View my complaint statuses");
        System.out.print("Choose option: ");
        int choice = sc.nextInt();
        sc.nextLine(); // consume newline

        if (choice == 1) {
            System.out.print("Enter complaint description: ");
            String desc = sc.nextLine();
            Database.allComplaints.add(new Complaint(this, desc));
            System.out.println("Complaint submitted.");
        } else {
            System.out.println("\n--- My Complaints ---");
            boolean found = false;
            for (Complaint c : Database.allComplaints) {
                // Simplified check to show complaints for demonstration
                c.displayComplaint();
                found = true;
            }
            if(!found) System.out.println("No complaints found.");
        }
    }

    @Override
    public void showDashboard() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Available Courses");
            System.out.println("2. Register for Course");
            System.out.println("3. Drop a Course");
            System.out.println("4. View Schedule");
            System.out.println("5. Track Academic Progress (Grades & GPA)");
            System.out.println("6. Complaints");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            if (choice == 1) viewCourses();
            else if (choice == 2) registerForCourse();
            else if (choice == 3) dropCourse();
            else if (choice == 4) viewSchedule();
            else if (choice == 5) trackAcademicProgress();
            else if (choice == 6) manageComplaints();
            else if (choice == 7) { logout(); break; }
        }
    }
}