package core;

import java.io.Serializable;

public abstract class User implements UserSession, Serializable {
    private static final long serialVersionUID = 1L;
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }

    @Override
    public boolean login(String emailInput, String passInput) {
        return this.email.equals(emailInput) && this.password.equals(passInput);
    }

    @Override
    public void logout() {
        System.out.println("Logging out " + email + "...");
    }

    public abstract void showDashboard();
}