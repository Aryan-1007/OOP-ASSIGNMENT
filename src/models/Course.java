package models;

import professor.Professor;
import student.Student;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    private String courseCode;
    private String title;
    private Professor professor;
    private int credits;
    private List<Course> prerequisites;
    private String timings;
    private int enrollmentLimit;
    private List<Student> enrolledStudents;
    private String syllabus = "TBA";
    private String location = "TBA";
    private String officeHours = "TBA";
    private List<Feedback<?>> feedbacks; // Generic feedback list

    public Course(String courseCode, String title, int credits, int limit) {
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
        this.enrollmentLimit = limit;
        this.prerequisites = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public Professor getProfessor() { return professor; }
    public List<Course> getPrerequisites() { return prerequisites; }
    public int getEnrollmentLimit() { return enrollmentLimit; }
    public List<Student> getEnrolledStudents() { return enrolledStudents; }
    public String getTimings() { return timings; }
    public List<Feedback<?>> getFeedbacks() { return feedbacks; }

    // Setters
    public void setProfessor(Professor p) { this.professor = p; }
    public void setTimings(String timings) { this.timings = timings; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setEnrollmentLimit(int limit) { this.enrollmentLimit = limit; }
    public void setOfficeHours(String hours) { this.officeHours = hours; }
    public void setLocation(String location) { this.location = location; }
    public void setSyllabus(String syllabus) { this.syllabus = syllabus; }

    // Core Functions
    public void addPrerequisite(Course c) { this.prerequisites.add(c); }
    public void enrollStudent(Student s) { this.enrolledStudents.add(s); }
    public void removeStudent(Student s) { this.enrolledStudents.remove(s); }
    public void addFeedback(Feedback<?> feedback) { this.feedbacks.add(feedback); }

    public void displayDetails() {
        System.out.println(courseCode + " - " + title + " (" + credits + " Credits)");
        System.out.println("Timings: " + (timings != null ? timings : "TBA") + " | Location: " + location);
        System.out.println("Professor: " + (professor != null ? professor.getEmail() : "TBA") + " | Office Hours: " + officeHours);
        System.out.println("Enrolled: " + enrolledStudents.size() + "/" + enrollmentLimit);
    }
}