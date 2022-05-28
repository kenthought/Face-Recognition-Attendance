package com.example.facerecognitionattendance.classes;

public class Student {
    public double id;
    public String first_name;
    public String middle_name;
    public String last_name;
    public String birthday;

    public Student(long id, String first_name, String middle_name, String last_name, String birthday) {
        this.id = id;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.birthday = birthday;

    }
}
