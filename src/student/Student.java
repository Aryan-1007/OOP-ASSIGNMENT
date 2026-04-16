package student;

import core.User;
import core.CourseManager;
import models.Course;
import models.Complaint;
import models.Feedback;
import utils.Database;
import exceptions.CourseFullException;
import exceptions.DropDeadlinePassedException;
import java.util.*;

public class Student extends User implements CourseManager {
    private static final long serialVersionUID = 1L;
    private int currentSemester = 1;
    private List<Course> registeredCourses = new ArrayList<>();
    private Map<Course, Integer> completedCourses = new HashMap<>();

    public Student(String email, String password) { super(email, password); }

    public void assignGrade(Course c, int grade) {
        if (registeredCourses.contains(c)) {
            registeredCourses.remove(c);
            c.removeStudent(this);
        }
        completedCourses.put(c, grade);
    }
    public Map<Course, Integer> getCompletedCourses() { return completedCourses; }

    @Override
    public void viewCourses() {
        System.out.println("\n--- Available Courses ---");
        for (Course c : Database.courseCatalog) {
            System.out.println(c.getCourseDetailsAsString());
            System.out.println("-");
        }
    }

    public void registerForCourse(int sem, String courseCode) throws CourseFullException {
        Course target = null;
        for (Course c : Database.courseCatalog) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode) && c.getSemester() == sem) {
                target = c;
                break;
            }
        }

        if (target == null) { throw new IllegalArgumentException("Course not found in this semester."); }

        if (target.getEnrolledStudents().size() >= target.getEnrollmentLimit()) {
            throw new CourseFullException("Course " + courseCode + " is already full!");
        }

        int currentCredits = registeredCourses.stream().mapToInt(Course::getCredits).sum();
        if (currentCredits + target.getCredits() > 20) {
            throw new IllegalArgumentException("Credit limit exceeded! Maximum is 20.");
        }

        for (Course prereq : target.getPrerequisites()) {
            if (!completedCourses.containsKey(prereq) || completedCourses.get(prereq) == 0) {
                throw new IllegalArgumentException("Prerequisite not met: " + prereq.getCourseCode());
            }
        }

        registeredCourses.add(target);
        target.enrollStudent(this);
    }

    public void dropCourse(String code) throws DropDeadlinePassedException {
        Course toDrop = null;
        for (Course c : registeredCourses) {
            if (c.getCourseCode().equalsIgnoreCase(code)) { toDrop = c; break; }
        }

        if (toDrop != null) {
            // Simulate deadline check for now
            if (new Random().nextBoolean()) {
                 throw new DropDeadlinePassedException("The deadline to drop " + code + " has already passed.");
            }
            registeredCourses.remove(toDrop);
            toDrop.removeStudent(this);
        } else {
            throw new IllegalArgumentException("You are not registered for this course.");
        }
    }

    public String getScheduleAsString() {
        if (registeredCourses.isEmpty()) return "No courses registered.";
        StringBuilder sb = new StringBuilder();
        for (Course c : registeredCourses) {
            sb.append(c.getCourseCode()).append(" | Prof: ")
              .append(c.getProfessor() != null ? c.getProfessor().getEmail() : "TBA")
              .append(" | Time: ").append(c.getTimings() != null ? c.getTimings() : "TBA")
              .append("\n");
        }
        return sb.toString();
    }

    public String getAcademicProgressAsString() {
        if (completedCourses.isEmpty()) {
            return "No completed courses yet. SGPA/CGPA: 0.0";
        }

        double totalPoints = 0;
        int totalCredits = 0;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Course, Integer> entry : completedCourses.entrySet()) {
            sb.append(entry.getKey().getCourseCode()).append(": Grade ").append(entry.getValue()).append("\n");
            int gradePoint = entry.getValue();
            totalPoints += (gradePoint * entry.getKey().getCredits());
            totalCredits += entry.getKey().getCredits();
        }
        sb.append("Current CGPA: ").append(totalPoints / totalCredits);
        return sb.toString();
    }
    
    public void submitFeedback(String courseCode, String feedbackText) {
        Course target = null;
        for (Course c : completedCourses.keySet()) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                target = c;
                break;
            }
        }
        if (target == null) {
            throw new IllegalArgumentException("You have not completed this course.");
        }
        Feedback<String> stringFeedback = new Feedback<>(this, feedbackText);
        target.addFeedback(stringFeedback);
    }

    public void submitComplaint(String description) {
        Database.allComplaints.add(new Complaint(this, description));
    }

    public String getComplaintsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (Complaint c : Database.allComplaints) {
            if (c.getStudent().equals(this)) {
                sb.append(c.toString()).append("\n");
                found = true;
            }
        }
        return found ? sb.toString() : "No complaints found.";
    }


    @Override
    public void showDashboard() {
        // This method is now only for the console version.
        // The GUI uses the new string-returning methods.
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Available Courses");
            System.out.println("2. Register for Course");
            System.out.println("3. Drop a Course");
            System.out.println("4. View Schedule");
            System.out.println("5. Track Academic Progress");
            System.out.println("6. Submit Course Feedback");
            System.out.println("7. Complaints");
            System.out.println("8. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            try {
                if (choice == 1) viewCourses();
                else if (choice == 2) {
                    System.out.print("Select semester: ");
                    int sem = sc.nextInt();
                    System.out.print("Enter course code: ");
                    String code = sc.next();
                    registerForCourse(sem, code);
                }
                else if (choice == 3) {
                    System.out.print("Enter course code: ");
                    String code = sc.next();
                    dropCourse(code);
                }
                else if (choice == 4) System.out.println(getScheduleAsString());
                else if (choice == 5) System.out.println(getAcademicProgressAsString());
                else if (choice == 6) {
                     System.out.print("Enter course code: ");
                    String code = sc.next();
                    sc.nextLine(); // consume newline
                    System.out.print("Enter feedback: ");
                    String feedback = sc.nextLine();
                    submitFeedback(code, feedback);
                }
                else if (choice == 7) {
                    System.out.println("1. Submit a new complaint\n2. View my complaint statuses");
                    int complaintChoice = sc.nextInt();
                    sc.nextLine();
                    if(complaintChoice == 1) {
                        System.out.print("Enter complaint: ");
                        String desc = sc.nextLine();
                        submitComplaint(desc);
                    } else {
                        System.out.println(getComplaintsAsString());
                    }
                }
                else if (choice == 8) { logout(); break; }
            } catch (Exception e) {
                System.out.println("\n[ERROR]: " + e.getMessage());
            }
        }
    }
}