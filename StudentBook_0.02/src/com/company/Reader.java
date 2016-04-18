package com.company;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by vadim on 30.03.2016.
 */
public class Reader {
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
        int sz = input.nextInt();
        res = new int[sz][];
        for (int i = 0; i < sz; i++)
            res[i] = nextIntArray();
        return res;
    }
    public int[] nextIntArray() {
        if (!scannerOk)
            return null;
        int[] res;
        int sz = input.nextInt();
        res = new int[sz];
        for (int i = 0; i < sz; i++){
            res[i] = input.nextInt();
        }
        return res;
    }
    public String[] nextStringArray() {
        if (!scannerOk)
            return null;
        String[] res;
        int sz = input.nextInt();
        input.nextLine();
        res = new String[sz];
        for (int i = 0; i < sz; i++){
            res[i] = input.nextLine();
        }
        return res;
    }
    public ChooseLessonMenu[] nextCLM_Array() {
        if (!scannerOk)
            return null;
        ChooseLessonMenu[] res;
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
    }
    public String nextTextFile(){
        if (!scannerOk)
            return null;
        String res = new String();
        while (input.hasNext())
            res += input.nextLine() + '\n';
        return res;
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

    public void closeReader(){
        input.close();
        scannerOk = false;
    }
}
