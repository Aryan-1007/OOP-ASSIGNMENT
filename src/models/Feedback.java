package models;

import student.Student;
import java.io.Serializable;

public class Feedback<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private Student student;
    private T feedbackData;

    public Feedback(Student student, T data) {
        this.student = student;
        this.feedbackData = data;
    }

    public Student getStudent() { return student; }
    public T getFeedbackData() { return feedbackData; }
}