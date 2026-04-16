package student;

import models.Course;
import utils.Database;
import exceptions.CourseFullException;
import exceptions.DropDeadlinePassedException;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class StudentDashboard extends JFrame {
    private Student student;

    public StudentDashboard(Student student) {
        this.student = student;
        setTitle("Student Dashboard - " + student.getEmail());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton viewCoursesButton = new JButton("View Available Courses");
        JButton registerCourseButton = new JButton("Register for Course");
        JButton dropCourseButton = new JButton("Drop a Course");
        JButton viewScheduleButton = new JButton("View Schedule");
        JButton trackProgressButton = new JButton("Track Academic Progress");
        JButton submitFeedbackButton = new JButton("Submit Course Feedback");
        JButton complaintsButton = new JButton("Complaints");
        JButton logoutButton = new JButton("Logout");

        panel.add(viewCoursesButton);
        panel.add(registerCourseButton);
        panel.add(dropCourseButton);
        panel.add(viewScheduleButton);
        panel.add(trackProgressButton);
        panel.add(submitFeedbackButton);
        panel.add(complaintsButton);
        panel.add(logoutButton);

        add(panel);

        viewCoursesButton.addActionListener(e -> viewAvailableCourses());
        registerCourseButton.addActionListener(e -> registerForCourse());
        dropCourseButton.addActionListener(e -> dropCourse());
        viewScheduleButton.addActionListener(e -> viewSchedule());
        trackProgressButton.addActionListener(e -> trackAcademicProgress());
        submitFeedbackButton.addActionListener(e -> submitFeedback());
        complaintsButton.addActionListener(e -> manageComplaints());

        logoutButton.addActionListener(e -> {
            student.logout();
            dispose();
        });
    }

    private void viewAvailableCourses() {
        JDialog dialog = new JDialog(this, "Available Courses", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        StringBuilder coursesText = new StringBuilder();
        for (Course c : Database.courseCatalog) {
            coursesText.append(c.getCourseDetailsAsString()).append("\n-\n");
        }
        textArea.setText(coursesText.toString());

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private void registerForCourse() {
        String semStr = JOptionPane.showInputDialog(this, "Enter semester to view courses (e.g., 1, 2):");
        if (semStr == null) return;

        try {
            int sem = Integer.parseInt(semStr);
            String courseCode = JOptionPane.showInputDialog(this, "Enter Course Code to register (e.g., CS101):");
            if (courseCode == null) return;

            student.registerForCourse(sem, courseCode);
            JOptionPane.showMessageDialog(this, "Successfully registered for " + courseCode);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid semester format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (CourseFullException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dropCourse() {
        String courseCode = JOptionPane.showInputDialog(this, "Enter Course Code to drop:");
        if (courseCode == null) return;

        try {
            student.dropCourse(courseCode);
            JOptionPane.showMessageDialog(this, "Successfully dropped " + courseCode);
        } catch (DropDeadlinePassedException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSchedule() {
        JDialog dialog = new JDialog(this, "Weekly Schedule", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        
        String schedule = student.getScheduleAsString();
        textArea.setText(schedule);

        dialog.add(new JScrollPane(textArea));
        dialog.setVisible(true);
    }

    private void trackAcademicProgress() {
        JDialog dialog = new JDialog(this, "Academic Progress", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        String progress = student.getAcademicProgressAsString();
        textArea.setText(progress);

        dialog.add(new JScrollPane(textArea));
        dialog.setVisible(true);
    }

    private void submitFeedback() {
        // Simplified feedback submission
        String courseCode = JOptionPane.showInputDialog(this, "Enter Course Code for feedback:");
        if (courseCode == null) return;

        String feedback = JOptionPane.showInputDialog(this, "Enter your feedback:");
        if (feedback == null) return;

        student.submitFeedback(courseCode, feedback);
        JOptionPane.showMessageDialog(this, "Feedback submitted successfully.");
    }

    private void manageComplaints() {
        String[] options = {"Submit a new complaint", "View my complaint statuses"};
        int choice = JOptionPane.showOptionDialog(this, "Choose an option", "Complaints",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            String desc = JOptionPane.showInputDialog(this, "Enter complaint description:");
            if (desc != null) {
                student.submitComplaint(desc);
                JOptionPane.showMessageDialog(this, "Complaint submitted.");
            }
        } else if (choice == 1) {
            JDialog dialog = new JDialog(this, "My Complaints", true);
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);

            String complaints = student.getComplaintsAsString();
            textArea.setText(complaints);

            dialog.add(new JScrollPane(textArea));
            dialog.setVisible(true);
        }
    }
}