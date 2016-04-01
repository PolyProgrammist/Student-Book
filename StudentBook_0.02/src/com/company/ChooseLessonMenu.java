package com.company;

/**
 * Created by vadim on 29.03.2016.
 */
public class ChooseLessonMenu {
    boolean lesson;
    String name;
    int[] children;
    int lessonID;

    public ChooseLessonMenu(String name, boolean lesson, int[] children) {
        this.name = name;
        this.lesson = lesson;
        this.children = children;
    }

    public ChooseLessonMenu() {
    }
}
