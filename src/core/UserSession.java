package core;

public interface UserSession {
    boolean login(String email, String pass);
    void logout();
}