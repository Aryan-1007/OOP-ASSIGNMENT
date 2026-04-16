import core.User;
import student.Student;
import student.TeachingAssistant;
import professor.Professor;
import administrator.Administrator;
import utils.Database;
import exceptions.InvalidLoginException;
import student.StudentDashboard;
import professor.ProfessorDashboard;
import administrator.AdministratorDashboard;
import student.TeachingAssistantDashboard; // Import TeachingAssistantDashboard

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainGUI extends JFrame {

    public MainGUI() {
        setTitle("University Course Registration System");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Database.saveData();
                System.out.println("Data saved. Exiting System...");
                e.getWindow().dispose();
                System.exit(0);
            }
        });

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel welcomeLabel = new JLabel("Welcome to the University Course Registration System", SwingConstants.CENTER);
        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");
        JButton exitButton = new JButton("Exit");

        panel.add(welcomeLabel);
        panel.add(new JLabel()); // Spacer
        panel.add(loginButton);
        panel.add(signUpButton);
        panel.add(exitButton);

        add(panel);

        loginButton.addActionListener(e -> showLoginDialog());
        signUpButton.addActionListener(e -> showSignUpDialog());
        exitButton.addActionListener(e -> {
            Database.saveData();
            System.out.println("Data saved. Exiting System...");
            dispose();
            System.exit(0);
        });
    }

    private void showLoginDialog() {
        JDialog loginDialog = new JDialog(this, "Login", true);
        loginDialog.setLayout(new GridLayout(4, 2, 10, 10));
        loginDialog.setSize(400, 200);
        loginDialog.setLocationRelativeTo(this);

        loginDialog.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        loginDialog.add(emailField);

        loginDialog.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        loginDialog.add(passwordField);

        loginDialog.add(new JLabel("Role:"));
        String[] roles = {"Student", "Professor", "Administrator", "Teaching Assistant"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        loginDialog.add(roleComboBox);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            int roleChoice = roleComboBox.getSelectedIndex() + 1;

            try {
                handleLogin(email, password, roleChoice);
                loginDialog.dispose();
            } catch (InvalidLoginException ex) {
                JOptionPane.showMessageDialog(loginDialog, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> loginDialog.dispose());

        loginDialog.add(loginButton);
        loginDialog.add(cancelButton);

        loginDialog.setVisible(true);
    }

    private void showSignUpDialog() {
        JDialog signUpDialog = new JDialog(this, "Sign Up", true);
        signUpDialog.setLayout(new GridLayout(4, 2, 10, 10));
        signUpDialog.setSize(400, 200);
        signUpDialog.setLocationRelativeTo(this);

        signUpDialog.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        signUpDialog.add(emailField);

        signUpDialog.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        signUpDialog.add(passwordField);

        signUpDialog.add(new JLabel("Role:"));
        String[] roles = {"Student", "Professor", "Teaching Assistant"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        signUpDialog.add(roleComboBox);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            int roleChoice = roleComboBox.getSelectedIndex() + 1;

            if (handleSignUp(email, password, roleChoice)) {
                JOptionPane.showMessageDialog(signUpDialog, "Sign up successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                signUpDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(signUpDialog, "An account with this email already exists!", "Sign Up Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> signUpDialog.dispose());

        signUpDialog.add(signUpButton);
        signUpDialog.add(cancelButton);

        signUpDialog.setVisible(true);
    }

    private boolean handleSignUp(String email, String password, int roleChoice) {
        for (User u : Database.allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return false; // User exists
            }
        }

        switch (roleChoice) {
            case 1:
                Database.allUsers.add(new Student(email, password));
                break;
            case 2:
                Database.allUsers.add(new Professor(email, password));
                break;
            case 3:
                Database.allUsers.add(new TeachingAssistant(email, password));
                break;
        }
        Database.saveData();

        return true;
    }

    private void handleLogin(String email, String password, int roleChoice) throws InvalidLoginException {
        User loggedInUser = null;
        for (User u : Database.allUsers) {
            if (u.login(email, password)) {
                if ((roleChoice == 1 && u instanceof Student && !(u instanceof TeachingAssistant)) ||
                    (roleChoice == 2 && u instanceof Professor) ||
                    (roleChoice == 3 && u instanceof Administrator) ||
                    (roleChoice == 4 && u instanceof TeachingAssistant)) {
                    loggedInUser = u;
                    break;
                }
            }
        }

        if (loggedInUser == null) {
            throw new InvalidLoginException("Incorrect email, password, or role selection.");
        }

        JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        this.setVisible(false);
        showGUIdashboard(loggedInUser);
    }

    private void showGUIdashboard(User user){
        JFrame dashboard;
        if (user instanceof Student && !(user instanceof TeachingAssistant)) {
            dashboard = new StudentDashboard((Student) user);
        } else if (user instanceof Professor) {
            dashboard = new ProfessorDashboard((Professor) user);
        } else if (user instanceof Administrator) {
            dashboard = new AdministratorDashboard((Administrator) user);
        } else if (user instanceof TeachingAssistant) {
            dashboard = new TeachingAssistantDashboard((TeachingAssistant) user);
        } else {
            // Fallback for any other user type
            JDialog genericDashboard = new JDialog(this, "Dashboard", true);
            genericDashboard.setSize(600, 400);
            genericDashboard.setLocationRelativeTo(this);
            genericDashboard.add(new JLabel("Welcome " + user.getEmail()));
            genericDashboard.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    genericDashboard.dispose();
                    MainGUI.this.setVisible(true);
                }
            });
            genericDashboard.setVisible(true);
            return;
        }

        dashboard.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                MainGUI.this.setVisible(true);
            }
        });
        dashboard.setVisible(true);
    }

    private static void initializeData() {
        Administrator admin = new Administrator("admin", "admin");
        Database.allUsers.add(admin);
    }

    public static void main(String[] args) {
        Database.loadData();

        if (Database.allUsers.isEmpty()) {
            initializeData();
        } else {
            boolean adminExists = false;
            for (User u : Database.allUsers) {
                if (u instanceof Administrator && u.getEmail().equals("admin")) {
                    adminExists = true;
                    break;
                }
            }
            if (!adminExists) {
                Database.allUsers.add(new Administrator("admin", "admin"));
            }
        }

        SwingUtilities.invokeLater(() -> {
            MainGUI mainGUI = new MainGUI();
            mainGUI.setVisible(true);
        });
    }
}