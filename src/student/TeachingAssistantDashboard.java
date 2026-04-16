package student;

import javax.swing.*;
import java.awt.*;

public class TeachingAssistantDashboard extends JFrame {
    private TeachingAssistant ta;

    public TeachingAssistantDashboard(TeachingAssistant ta) {
        this.ta = ta;
        setTitle("TA Dashboard - " + ta.getEmail());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if (ta.getAssignedCourse() == null) {
            panel.add(new JLabel("You are not yet assigned to a course."));
        } else {
            panel.add(new JLabel("Assigned to: " + ta.getAssignedCourse().getCourseCode()));
            JButton viewEnrolledButton = new JButton("View Enrolled Students");
            JButton assignGradeButton = new JButton("Assign Grade");
            panel.add(viewEnrolledButton);
            panel.add(assignGradeButton);

            viewEnrolledButton.addActionListener(e -> viewEnrolledStudents());
            assignGradeButton.addActionListener(e -> assignGrade());
        }

        JButton logoutButton = new JButton("Logout");
        panel.add(logoutButton);

        add(panel);

        logoutButton.addActionListener(e -> {
            ta.logout();
            dispose();
        });
    }

    private void viewEnrolledStudents() {
        JTextArea textArea = new JTextArea(ta.getEnrolledStudentsAsString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, scrollPane, "Enrolled Students", JOptionPane.INFORMATION_MESSAGE);
    }

    private void assignGrade() {
        String studentEmail = JOptionPane.showInputDialog(this, "Enter Student's Email:");
        if (studentEmail == null || studentEmail.trim().isEmpty()) return;

        String gradeStr = JOptionPane.showInputDialog(this, "Enter Grade (0-10):");
        if (gradeStr == null) return;

        try {
            int grade = Integer.parseInt(gradeStr);
            ta.assignGrade(studentEmail, grade);
            JOptionPane.showMessageDialog(this, "Grade assigned successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid grade format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}