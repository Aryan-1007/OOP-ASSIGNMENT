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

    public void resolveComplaint(String resolution) {
        this.status = "Resolved";
        this.resolutionDetails = resolution;
    }

    public void displayComplaint() {
        System.out.println("ID: " + complaintID + " | By: " + student.getEmail() + " | Status: " + status);
        System.out.println("Issue: " + description);
        System.out.println("Resolution: " + resolutionDetails);
        System.out.println("--------------------------------------------------");
    }
}