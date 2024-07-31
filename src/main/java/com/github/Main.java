package com.github;


import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {
        Student student = Student.builder()
                .name("Niloy")
                .id("1921723642")
                .courses(List.of("CSE 115", "MAT120"))
                .build();


        try {

            Object value = ConsoleUiObjectEditor.displayAndEditObject(student);
            System.out.println(value.toString());
            System.out.println("-----------------_FROM MAIN_--------------------");
            System.err.println(value);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        //log output


    }
}