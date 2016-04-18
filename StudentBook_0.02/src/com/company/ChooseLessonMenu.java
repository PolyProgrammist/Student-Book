package com.company;

public class ChooseLessonMenu {
    boolean lesson;
    String name;
    int[] children;
    int lessonID;
    double studiedRatio;

    public ChooseLessonMenu(String name, boolean lesson, int[] children) {
        this.name = name;
        this.lesson = lesson;
        this.children = children;


    }
    public ChooseLessonMenu() {
    }
}
