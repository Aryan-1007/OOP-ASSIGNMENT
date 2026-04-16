package administrator;

import core.User;
import core.CourseManager;
import models.Course;
import models.Complaint;
import professor.Professor;
import student.Student;
import utils.Database;
import java.io.Serial;
import java.util.*;

public class Administrator extends User implements CourseManager {
    @Serial
    private static final long serialVersionUID = 1L;

    public Administrator(String email, String password) {
        super(email, password);
    }

    public String getCatalogAsString() {
        StringBuilder sb = new StringBuilder();
        for (Course c : Database.courseCatalog) {
            sb.append(c.getCourseDetailsAsString()).append("\n-\n");
        }
        return sb.toString();
    }

    public void addCourse(String code, String title, int credits, int limit, int semester, String[] prereqCodes) {
        Course newCourse = new Course(code, title, credits, limit, semester);
        if (prereqCodes != null) {
            for (String prereqCode : prereqCodes) {
                for (Course c : Database.courseCatalog) {
                    if (c.getCourseCode().equalsIgnoreCase(prereqCode.trim())) {
                        newCourse.addPrerequisite(c);
                        break;
                    }
                }
            }
        }
        Database.courseCatalog.add(newCourse);
    }

    public void deleteCourse(String code) {
        Database.courseCatalog.removeIf(c -> c.getCourseCode().equalsIgnoreCase(code));
    }

    public void assignProfessorToCourse(String courseCode, String profEmail) {
        Course targetCourse = null;
        for (Course c : Database.courseCatalog) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode)) {
                targetCourse = c;
                break;
            }
        }
        if (targetCourse == null) return;

        Professor targetProf = null;
        for (User u : Database.allUsers) {
            if (u instanceof Professor && u.getEmail().equalsIgnoreCase(profEmail)) {
                targetProf = (Professor) u;
                break;
            }
        }
        if (targetProf == null) return;

        targetProf.assignCourse(targetCourse);
    }

    public String getComplaintsAsString() {
        StringBuilder sb = new StringBuilder();
        for (Complaint c : Database.allComplaints) {
            sb.append(c.toString()).append("\n");
        }
        return sb.toString();
    }

    public String getAllUsersAsString() {
        StringBuilder sb = new StringBuilder();
        for (User u : Database.allUsers) {
            sb.append("[").append(u.getClass().getSimpleName()).append("] ").append(u.getEmail()).append("\n");
        }
        return sb.toString();
    }

    public void deleteUser(String email) {
        if (this.getEmail().equalsIgnoreCase(email)) return;

        User userToDelete = null;
        for (User u : Database.allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                userToDelete = u;
                break;
            }
        }
        if (userToDelete == null) return;

        if (userToDelete instanceof Student student) {
            for (Course c : Database.courseCatalog) {
                c.removeStudent(student);
            }
        }

        Database.allUsers.remove(userToDelete);
        Database.saveData();
    }

    @Override
    public void viewCourses() {
        System.out.println(getCatalogAsString());
    }

    @Override
    public void showDashboard() {
        // Console implementation
    }
}