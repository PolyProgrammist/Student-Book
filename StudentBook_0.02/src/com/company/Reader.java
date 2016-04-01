package com.company;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by vadim on 30.03.2016.
 */
public class Reader {
    public static final String DEFAULT_TREES_WAY = "src\\LessonTrees\\";
    public static final String DEFAULT_CONNECTIONS_FILE_NAME = "Connections.aai";
    public static final String DEFAULT_TOPICS_FILE_NAME = "Topics.sbclm";
    public static final String DEFAULT_CLASSES_FILE_NAME = "Classes.sbclm";
    public static final String DEFAULT_ID_TO_LES_FILE_NAME = "idToLessonFileName.as";
    public static final String DEFAULT_LESSONS_WAY = "src\\Lessons\\";
    public static final String LESSON_EXTENSION = ".sblsnx";
    public static final String DEFAULT_PROF_INFO_WAY = "src\\ProfilesInfo\\";
    public static final String DEFAULT_PROFILES_FILE_NAME = "Profiles.as";

    private String way;
    private String fileName;

    private Scanner input;
    private boolean scannerOk = false;

    public Reader(String way, String fileName) {
        setPath(way, fileName);
    }
    public int[][] nextIntArrayArray() {
        if (!scannerOk)
            return null;
        int[][] res;
        try {
            int sz = input.nextInt();
            res = new int[sz][];
            for (int i = 0; i < sz; i++){
                int nowsz = input.nextInt();
                res[i] = new int[nowsz];
                for (int j = 0; j < nowsz; j++)
                    res[i][j] = input.nextInt();
            }
            return res;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage(), "Reader.nextIntArrayArray()", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    public int[] nextIntArray() {
        if (!scannerOk)
            return null;
        int[] res;
        try {
            int sz = input.nextInt();
            res = new int[sz];
            for (int i = 0; i < sz; i++){
                res[i] = input.nextInt();
            }
            return res;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage(), "Reader.nextIntArrayArray()", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    public ChooseLessonMenu[] nextCLM_Array() {
        if (!scannerOk)
            return null;
        ChooseLessonMenu[] res;
        try {
            int sz = input.nextInt();
            res = new ChooseLessonMenu[sz];
            for (int i = 0; i < sz; i++){
                res[i] = new ChooseLessonMenu();
                res[i].lesson = input.nextBoolean();
                input.nextLine();
                res[i].name = new String(input.nextLine());
                if (!res[i].lesson) {
                    int sznow = input.nextInt();
                    res[i].children = new int[sznow];
                    for (int j = 0; j < sznow; j++)
                        res[i].children[j] = input.nextInt();
                }
                else {
                    res[i].lessonID = input.nextInt();
                }
            }
            return res;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage(), "Reader.nextCLM_Array()", JOptionPane.ERROR_MESSAGE);
            return new ChooseLessonMenu[]{new ChooseLessonMenu("Error", false, null)};
        }
    }
    public void setFileName(String fileName) {
        setPath(this.way, fileName);
    }

    public void setPath(String way, String fileName){
        this.way = way;
        this.fileName = fileName;
        if (scannerOk)
            input.close();
        File file = new File(String.format("%s%s", way, fileName));
        try {
            input = new Scanner(file);
            scannerOk = true;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage(), "Reader.setPath()", JOptionPane.ERROR_MESSAGE);
            scannerOk = false;
        }
    }

    public String[] nextStringArray() {
        if (!scannerOk)
            return null;
        String[] res;
        try {
            int sz = input.nextInt();
            input.nextLine();
            res = new String[sz];
            for (int i = 0; i < sz; i++){
                res[i] = input.nextLine();
            }
            return res;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JPanel(), e.getMessage(), "Reader.nextStringArray()", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
