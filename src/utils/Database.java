package utils;

import models.Course;
import models.Complaint;
import core.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public static List<Course> courseCatalog = new ArrayList<>();
    public static List<User> allUsers = new ArrayList<>();
    public static List<Complaint> allComplaints = new ArrayList<>();

    private static final String FILE_NAME = "database.ser";

    public static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(courseCatalog);
            oos.writeObject(allUsers);
            oos.writeObject(allComplaints);
        } catch (IOException e) {
            System.out.println("Error saving database: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            courseCatalog = (List<Course>) ois.readObject();
            allUsers = (List<User>) ois.readObject();
            allComplaints = (List<Complaint>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading database: " + e.getMessage());
        }
    }
}