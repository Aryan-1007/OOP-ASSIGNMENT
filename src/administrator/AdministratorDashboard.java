package administrator;

import javax.swing.*;
import java.awt.*;

public class AdministratorDashboard extends JFrame {
    private Administrator admin;

    public AdministratorDashboard(Administrator admin) {
        this.admin = admin;
        setTitle("Administrator Dashboard - " + admin.getEmail());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton manageCoursesButton = new JButton("Manage Courses");
        JButton manageUsersButton = new JButton("Manage Users");
        JButton viewComplaintsButton = new JButton("View All Complaints");
        JButton assignProfButton = new JButton("Assign Professor to Course");
        JButton logoutButton = new JButton("Logout");

        panel.add(manageCoursesButton);
        panel.add(manageUsersButton);
        panel.add(viewComplaintsButton);
        panel.add(assignProfButton);
        panel.add(logoutButton);

        add(panel);

        manageCoursesButton.addActionListener(e -> manageCourses());
        manageUsersButton.addActionListener(e -> manageUsers());
        viewComplaintsButton.addActionListener(e -> viewAllComplaints());
        assignProfButton.addActionListener(e -> assignProfessorToCourse());

        logoutButton.addActionListener(e -> {
            admin.logout();
            dispose();
        });
    }

    private void manageCourses() {
        String[] options = {"View Course Catalog", "Add a New Course", "Delete a Course"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a course management action:", "Manage Courses",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0: // View Catalog
                JTextArea textArea = new JTextArea(admin.getCatalogAsString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                textArea.setEditable(false);
                JOptionPane.showMessageDialog(this, scrollPane, "Course Catalog", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 1: // Add Course
                addCourseDialog();
                break;
            case 2: // Delete Course
                String courseCode = JOptionPane.showInputDialog(this, "Enter the Course Code to delete:");
                if (courseCode != null && !courseCode.trim().isEmpty()) {
                    admin.deleteCourse(courseCode);
                    JOptionPane.showMessageDialog(this, "Course deleted successfully (if it existed).");
                }
                break;
        }
    }

    private void addCourseDialog() {
        JTextField codeField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField creditsField = new JTextField();
        JTextField limitField = new JTextField();
        JTextField semesterField = new JTextField();
        JTextField prereqsField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Course Code:"));
        panel.add(codeField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Credits:"));
        panel.add(creditsField);
        panel.add(new JLabel("Enrollment Limit:"));
        panel.add(limitField);
        panel.add(new JLabel("Semester:"));
        panel.add(semesterField);
        panel.add(new JLabel("Prerequisites (comma-separated):"));
        panel.add(prereqsField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Course",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String code = codeField.getText();
                String title = titleField.getText();
                int credits = Integer.parseInt(creditsField.getText());
                int limit = Integer.parseInt(limitField.getText());
                int semester = Integer.parseInt(semesterField.getText());
                String[] prereqs = prereqsField.getText().isEmpty() ? null : prereqsField.getText().split(",");
                
                admin.addCourse(code, title, credits, limit, semester, prereqs);
                JOptionPane.showMessageDialog(this, "Course added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format for credits, limit, or semester.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void manageUsers() {
        String[] options = {"View All Users", "Delete a User"};
        int choice = JOptionPane.showOptionDialog(this, "Choose a user management action:", "Manage Users",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) { // View Users
            JTextArea textArea = new JTextArea(admin.getAllUsersAsString());
            JScrollPane scrollPane = new JScrollPane(textArea);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, scrollPane, "All Users", JOptionPane.INFORMATION_MESSAGE);
        } else if (choice == 1) { // Delete User
            String email = JOptionPane.showInputDialog(this, "Enter the email of the user to delete:");
            if (email != null && !email.trim().isEmpty()) {
                admin.deleteUser(email);
                JOptionPane.showMessageDialog(this, "User deleted successfully (if they existed).");
            }
        }
    }

    private void viewAllComplaints() {
        JTextArea textArea = new JTextArea(admin.getComplaintsAsString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, scrollPane, "All Complaints", JOptionPane.INFORMATION_MESSAGE);
    }

    private void assignProfessorToCourse() {
        String courseCode = JOptionPane.showInputDialog(this, "Enter Course Code:");
        if (courseCode == null || courseCode.trim().isEmpty()) return;

        String profEmail = JOptionPane.showInputDialog(this, "Enter Professor's Email:");
        if (profEmail == null || profEmail.trim().isEmpty()) return;

        try {
            admin.assignProfessorToCourse(courseCode, profEmail);
            JOptionPane.showMessageDialog(this, "Professor assigned successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}