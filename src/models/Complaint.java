package models;

import student.Student;
import java.io.Serializable;

public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int counter = 1;
    private int complaintID;
    private Student student;
    private String description;
    private String status;
    private String resolutionDetails = "None";

    public Complaint(Student student, String description) {
        this.complaintID = counter++;
        this.student = student;
        this.description = description;
        this.status = "Pending";
    }

    public int getComplaintID() { return complaintID; }
    public String getStatus() { return status; }
    public Student getStudent() { return student; }


    public void resolveComplaint(String resolution) {
        this.status = "Resolved";
        this.resolutionDetails = resolution;
    }

    public void displayComplaint() {
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return "ID: " + complaintID + " | By: " + student.getEmail() + " | Status: " + status + "\n" +
               "Issue: " + description + "\n" +
               "Resolution: " + resolutionDetails + "\n" +
               "--------------------------------------------------";
    }
}