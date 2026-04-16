package professor;

import javax.swing.*;
import java.awt.*;

public class ProfessorDashboard extends JFrame {
    private Professor professor;

    public ProfessorDashboard(Professor professor) {
        this.professor = professor;
        setTitle("Professor Dashboard - " + professor.getEmail());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton viewCoursesButton = new JButton("View My Courses");
        JButton updateCourseButton = new JButton("Update Course Details");
        JButton viewEnrolledButton = new JButton("View Enrolled Students");
        JButton viewFeedbackButton = new JButton("View Course Feedback");
        JButton assignTaButton = new JButton("Assign TA to Course");
        JButton assignGradeButton = new JButton("Assign Grade");
        JButton logoutButton = new JButton("Logout");

        panel.add(viewCoursesButton);
        panel.add(updateCourseButton);
        panel.add(viewEnrolledButton);
        panel.add(viewFeedbackButton);
        panel.add(assignTaButton);
        panel.add(assignGradeButton);
        panel.add(logoutButton);

        add(panel);

        viewCoursesButton.addActionListener(e -> viewMyCourses());
        updateCourseButton.addActionListener(e -> updateCourseDetails());
        viewEnrolledButton.addActionListener(e -> viewEnrolledStudents());
        viewFeedbackButton.addActionListener(e -> viewCourseFeedback());
        assignTaButton.addActionListener(e -> assignTaToCourse());
        assignGradeButton.addActionListener(e -> assignGrade());

        logoutButton.addActionListener(e -> {
            professor.logout();
            dispose();
        });
    }

    private void viewMyCourses() {
        JTextArea textArea = new JTextArea(professor.getMyCoursesAsString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, scrollPane, "My Assigned Courses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateCourseDetails() {
        String courseCode = JOptionPane.showInputDialog(this, "Enter Course Code to update:");
        if (courseCode == null || courseCode.trim().isEmpty()) return;

        String[] options = {"Syllabus", "Timings", "Enrollment Limit", "Office Hours"};
        int choice = JOptionPane.showOptionDialog(this, "Choose detail to update:", "Update Course",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == -1) return;

        String newValue = JOptionPane.showInputDialog(this, "Enter new value for " + options[choice] + ":");
        if (newValue == null) return;

        try {
            professor.updateCourseDetails(courseCode, choice + 1, newValue);
            JOptionPane.showMessageDialog(this, "Course updated successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Update Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewEnrolledStudents() {
        JTextArea textArea = new JTextArea(professor.getEnrolledStudentsAsString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, scrollPane, "Enrolled Students", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewCourseFeedback() {
        JTextArea textArea = new JTextArea(professor.getCourseFeedbackAsString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, scrollPane, "Course Feedback", JOptionPane.INFORMATION_MESSAGE);
    }

    private void assignTaToCourse() {
        String courseCode = JOptionPane.showInputDialog(this, "Enter Course Code:");
        if (courseCode == null || courseCode.trim().isEmpty()) return;

        String taEmail = JOptionPane.showInputDialog(this, "Enter TA's Email:");
        if (taEmail == null || taEmail.trim().isEmpty()) return;

        try {
            professor.assignTaToCourse(courseCode, taEmail);
            JOptionPane.showMessageDialog(this, "TA assigned successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignGrade() {
        String courseCode = JOptionPane.showInputDialog(this, "Enter Course Code:");
        if (courseCode == null || courseCode.trim().isEmpty()) return;

        String studentEmail = JOptionPane.showInputDialog(this, "Enter Student's Email:");
        if (studentEmail == null || studentEmail.trim().isEmpty()) return;

        String gradeStr = JOptionPane.showInputDialog(this, "Enter Grade (0-10):");
        if (gradeStr == null) return;

        try {
            int grade = Integer.parseInt(gradeStr);
            professor.assignGrade(courseCode, studentEmail, grade);
            JOptionPane.showMessageDialog(this, "Grade assigned successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid grade format.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}