package com.github;



import java.io.IOException;
import java.util.List;



public class Main {

    public static void main(String[] args) throws IOException {
        Student student = Student.builder()
                .name("Niloy")
                .id("1921723642")
                .courses(List.of("CSE 115", "MAT120"))
                .build();


        Object value = LanternaFileEditor.displayAndEditFile(student);
        //log output
        System.out.println(value.toString());
        System.out.println("-----------------_FROM MAIN_--------------------");

        System.err.println(value);

    }
}