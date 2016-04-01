package com.company;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;

/**
 * Created by vadim on 28.03.2016.
 */
public class MainMenuBar extends JFrame{
    JFrame frame = new JFrame("StudentBook");
    JMenuBar mainMenuBar = new JMenuBar();
    JTextPane mainLessonTextPane = new JTextPane();
    JLabel tmpLabel = new JLabel();
    JMenu topicsMenu = new JMenu(), classesMenu = new JMenu();
    String profileName = null;
    boolean[] haveStudied_fileData;
    boolean[] haveStudied;
    int nowLessonID;
    Stack<Pair<Integer, Integer>> studyValueChangingStack;
    static final Color studiedColor = Color.green;
    static final Color unstudiedColor = new Color(255, 130, 130);

    public MainMenuBar(){
        mainMenuBar.add(topicsMenu);
        mainMenuBar.add(classesMenu);
        InitializeProfileEnvironment();
        mainMenuBar.add(profilesJMenu(Main.profiles));
        frame.setJMenuBar(mainMenuBar);
        frame.setLayout(new BorderLayout());
        tmpLabel.setText("");
        tmpLabel.setOpaque(true);
        frame.add(tmpLabel, BorderLayout.NORTH);
        mainLessonTextPane.setText("Your ad could be here");
        mainLessonTextPane.setEditable(false);
        frame.add(mainLessonTextPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }
    private void InitializeProfileEnvironment(){
        if (profileName != null){
            Reader rd = new Reader(Reader.DEFAULT_PROF_INFO_WAY, String.format("%s.ai", profileName));
            haveStudied_fileData = GoodFunctions.intArrayToBooleanArray(rd.nextIntArray(), Main.lessonFileName.length);
            for (int i = 0; i < haveStudied_fileData.length; i++) {
                System.out.print(haveStudied_fileData[i]);
                System.out.print(" ");
            }
            System.out.println();
            HaveStudiedCalc hsc = new HaveStudiedCalc(haveStudied_fileData, Main.anticonnections);
            System.out.println("connections");
            for (int i = 0; i < Main.connections.length; i++){
                System.out.print(i);
                System.out.print(" :  ");
                for (int j = 0; j < Main.connections[i].length; j++){
                    System.out.print(Main.connections[i][j]);
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println("anticonnections");
            for (int i = 0; i < Main.anticonnections.length; i++){
                System.out.print(i);
                System.out.print(" :  ");
                for (int j = 0; j < Main.anticonnections[i].length; j++){
                    System.out.print(Main.anticonnections[i][j]);
                    System.out.print(" ");
                }
                System.out.println();
            }
            haveStudied = hsc.getFinished();
            System.out.println();
            for (int i = 0; i < haveStudied.length; i++) {
                System.out.print(haveStudied[i]);
                System.out.print(" ");
            }
            studyValueChangingStack = new Stack<Pair<Integer, Integer>>();
        }
        else
            studyValueChangingStack = null;
        mainMenuBar.remove(topicsMenu);
        mainMenuBar.remove(classesMenu);
        topicsMenu = addChooseLessonMenu(Main.topics, 0);
        classesMenu = addChooseLessonMenu(Main.classes, 0);
        mainMenuBar.add(classesMenu, 0);
        mainMenuBar.add(topicsMenu, 0);
    }
    private boolean studiedLesson(int lid){ //bad way
        if (profileName == null)
            return false;
        return haveStudied[lid];
    }
    private JMenu profilesJMenu(String[] profiles){
        JMenu res = new JMenu("Profiles");
        for (int i = 0; i < profiles.length; i++){
            JMenuItem jmi = new JMenuItem(profiles[i]);
            jmi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    profileName = jmi.getText();
                    InitializeProfileEnvironment();
                    mainMenuBar.getMenu(2).setText(profileName);
                    addLesson(Main.lessonFileName[nowLessonID], studiedLesson(nowLessonID));
                }
            });
            res.add(jmi);
        }
        return res;
    }
    private void addLesson(String name, boolean done){
        String s = Reader.DEFAULT_LESSONS_WAY + name + Reader.LESSON_EXTENSION;
        try {
            File file = new File(s);
            String res = new String();
            Scanner sc = new Scanner(file);
            while (sc.hasNext())
                res += sc.nextLine() + '\n';
            mainLessonTextPane.setText(res);
        } catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(new JPanel(), String.format("Could not open file %s\n%s", s, e1.getMessage()), "addLesson", JOptionPane.ERROR_MESSAGE);
        }
    }
    private JMenu addChooseLessonMenu(ChooseLessonMenu[] clm, int v) {
        JMenu jm = new JMenu(clm[v].name);
        jm.setOpaque(true);
        int countStudied = 0;
        if (clm[v].children != null)
        for (int i = 0; i < clm[v].children.length; i++) {
            int u = clm[v].children[i];
            if (clm[u].children == null || clm[u].children.length == 0){
                JMenuItem jmi = new JMenuItem(clm[u].name);
                jmi.setOpaque(true);
                if (clm[u].lesson) {
                    if (Main.lessonFileName == null || clm[u].lessonID >= Main.lessonFileName.length)
                        continue;
                    boolean done = studiedLesson(clm[u].lessonID);
                    if (profileName != null) {

                        if (done) {
                            jmi.setBackground(studiedColor);
                            countStudied++;
                        }
                        else
                            jmi.setBackground(unstudiedColor);
                    }
                    final boolean finalDone = done;
                    jmi.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            nowLessonID = clm[u].lessonID;
                            addLesson(Main.lessonFileName[nowLessonID], finalDone);
                            if (finalDone) {
                                tmpLabel.setBackground(studiedColor);
                                tmpLabel.setText("Studied");
                            } else {
                                tmpLabel.setBackground(Color.red);
                                tmpLabel.setText("");
                            }
                            frame.repaint();
                            tmpLabel.updateUI();
                        }
                    });
                }
                else {
                    if (profileName != null)
                        jmi.setBackground(Color.green);
                    countStudied++;
                }
                jm.add(jmi);
            }
            else {
                JMenu addinJM = addChooseLessonMenu(clm, clm[v].children[i]);
                jm.add(addinJM);
                if (addinJM.getBackground() == Color.green)
                    countStudied++;
            }
        }
        if (profileName != null && v != 0){
            if (clm[v].children == null || clm[v].children.length == countStudied)
                jm.setBackground(studiedColor);
            else
                jm.setBackground(unstudiedColor);
        }
        return jm;
    }

    public static void launch(){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                new MainMenuBar();
            }
        });
    }
}
