package com.github;



import java.util.List;



public class Main {

    public static void main(String[] args) throws Exception {
        Student student = Student.builder()
                .name("Niloy")
                .id("1921723642")
                .courses(List.of("CSE 115", "MAT120"))
                .build();
        System.out.println("-----------------_FROM MAIN_--------------------");
        System.out.println("Hello I am running BEFORE terminal");

        Object value = ConsoleObjectEditor.displayAndEditObjectInConsole(student);
        //log output
        System.out.println(value.toString());
        System.out.println("Hello I am running AFTER terminal");

        System.err.println(value);

    }
}