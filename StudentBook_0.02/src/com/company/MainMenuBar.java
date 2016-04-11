package com.company;

import javafx.util.Pair;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

import static java.lang.System.exit;

/**
 * Created by vadim on 28.03.2016.
 */
public class MainMenuBar extends JFrame{
    JFrame frame = new JFrame("StudentBook");
    JMenuBar mainMenuBar = new JMenuBar();
    JTextPane mainLessonTextPane = new JTextPane();
    JLabel tmpLabel = new JLabel();
    JPanel tmpPane = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
    JCheckBox tmpCheckBox = new JCheckBox();
    JMenu topicsMenu = new JMenu(), classesMenu = new JMenu();
    String profileName = null;
    boolean[] haveStudied_fileDataInitial;
    boolean[] haveStudied;
    int nowLessonID = -1;
    boolean studiedChanged = false;

    public MainMenuBar(){

        mainMenuBar.add(topicsMenu);
        mainMenuBar.add(classesMenu);
        InitializeProfileEnvironment();
        mainMenuBar.add(profilesJMenu(Main.profiles));
        frame.setJMenuBar(mainMenuBar);
        frame.setLayout(new BorderLayout());
        tmpLabel.setText("");
        tmpLabel.setOpaque(true);
        tmpPane.add(tmpCheckBox);
        tmpPane.add(tmpLabel);
        tmpCheckBox.setVisible(false);
        tmpCheckBox.setOpaque(true);
        tmpCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lessonStudiedChange(tmpCheckBox.isSelected());
            }
        });
        frame.add(tmpPane, BorderLayout.NORTH);
        mainLessonTextPane.setText("Your ad could be here");
        mainLessonTextPane.setEditable(false);
        frame.add(mainLessonTextPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (profileName == null || !studiedChanged)
                    System.exit(0);
                int opt = JOptionPane.showConfirmDialog(frame, "Do you want to save changes?", "So, save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opt == JOptionPane.NO_OPTION)
                    System.exit(0);
                else if (opt == JOptionPane.YES_OPTION){
                    saveProfileChanges();
                    System.exit(0);
                }
            }
        });
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }

    private void saveProfileChanges() {
        if (profileName == null)
            return;
        try {
            PrintWriter writer = new PrintWriter(Reader.DEFAULT_PROF_INFO_WAY + profileName + ".ai");
            int sz = 0;
            for (int i = 0; i < haveStudied.length; i++)
                sz += haveStudied[i] ? 1 : 0;
            writer.println(sz);
            for (int i = 0; i < haveStudied.length; i++)
                if (haveStudied[i])
                    writer.println(i);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void lessonStudiedChange(boolean selected) {
        studiedChanged = true;
        fillStudied(selected);
        haveStudied[nowLessonID] = selected;
        HaveStudiedCalc hsc = new HaveStudiedCalc(haveStudied, Main.connections, Main.anticonnections);
        haveStudied = hsc.getChange(nowLessonID, selected);
        updateMainMenuBar();
    }

    private void fillStudied(boolean selected) {
        double d = selected ? 1 : 0;
        tmpLabel.setBackground(GoodFunctions.getRedToGreen(d));
        tmpCheckBox.setBackground(GoodFunctions.getRedToGreen(d));
        tmpPane.setBackground(GoodFunctions.getRedToGreen(d));
        tmpLabel.setText(selected ? "Studied" : "Not Studied");
    }
    void printhaveStudied(){
        if (haveStudied != null) {
            System.out.println("HaveStudied:");
            for (int i = 0; i < haveStudied.length; i++)
                System.out.printf("%s ", haveStudied[i]);
            System.out.println();
        }
    }
    public void updateMainMenuBar(){
        mainMenuBar.remove(topicsMenu);
        mainMenuBar.remove(classesMenu);
        topicsMenu = addChooseLessonMenu(Main.topics, 0);
        classesMenu = addChooseLessonMenu(Main.classes, 0);
        mainMenuBar.add(classesMenu, 0);
        mainMenuBar.add(topicsMenu, 0);
    }
    private void InitializeProfileEnvironment(){
        if (profileName != null){
            Reader rd = new Reader(Reader.DEFAULT_PROF_INFO_WAY, String.format("%s.ai", profileName));
            haveStudied_fileDataInitial = GoodFunctions.intArrayToBooleanArray(rd.nextIntArray(), Main.lessonFileName.length);
            HaveStudiedCalc hsc = new HaveStudiedCalc(haveStudied_fileDataInitial, Main.connections, Main.anticonnections);
            haveStudied = hsc.getFullFinished();
        }
        updateMainMenuBar();
    }

    private JMenu profilesJMenu(String[] profiles){
        JMenu res = new JMenu("Profiles");
        for (int i = 0; i < profiles.length; i++){
            JMenuItem jmi = new JMenuItem(profiles[i]);
            jmi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (profileName != null && studiedChanged) {
                        int opt = JOptionPane.showConfirmDialog(frame, "Do you want to save changes?", "So, save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (opt == JOptionPane.CANCEL_OPTION)
                            return;
                        else if (opt == JOptionPane.YES_OPTION)
                            saveProfileChanges();
                    }
                    studiedChanged = false;
                    profileName = jmi.getText();
                    InitializeProfileEnvironment();
                    mainMenuBar.getMenu(2).setText(profileName);
                    if (nowLessonID != -1)
                        addLesson(Main.lessonFileName[nowLessonID], haveStudied[nowLessonID]);
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
        if (profileName != null) {
            tmpCheckBox.setVisible(true);
            tmpCheckBox.setSelected(done);
            fillStudied(done);
        }
    }
    private JMenu addChooseLessonMenu(ChooseLessonMenu[] clm, int v) {
        JMenu jm = new JMenu(clm[v].name);
        jm.setOpaque(true);
        double studiedRatio = 0;
        if (clm[v].children != null) {
            for (int i = 0; i < clm[v].children.length; i++) {
                int u = clm[v].children[i];
                if (clm[u].children == null || clm[u].children.length == 0) {
                    JMenuItem jmi = new JMenuItem(clm[u].name);
                    jmi.setOpaque(true);
                    if (clm[u].lesson) {
                        if (Main.lessonFileName == null || clm[u].lessonID >= Main.lessonFileName.length)
                            continue;
                        boolean done = profileName == null ? false : haveStudied[clm[u].lessonID];
                        if (profileName != null)
                                jmi.setBackground(GoodFunctions.getRedToGreen(done ? 1 : 0));
                        studiedRatio += done ? 1 : 0;
                        final boolean finalDone = done;
                        jmi.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                nowLessonID = clm[u].lessonID;
                                addLesson(Main.lessonFileName[nowLessonID], finalDone);
                            }
                        });
                    } else {
                        if (profileName != null)
                            jmi.setBackground(GoodFunctions.getRedToGreen(1));
                        jmi.setEnabled(false);
                        studiedRatio += 1;
                    }
                    jm.add(jmi);
                } else {
                    JMenu addinJM = addChooseLessonMenu(clm, u);
                    jm.add(addinJM);
                    studiedRatio += (clm[u].children == null) ? 1 : clm[u].studiedRatio;
                }
            }
        }
        clm[v].studiedRatio = studiedRatio / (double)clm[v].children.length;
        if (profileName != null && v != 0){
            if (clm[v].children == null)
                jm.setBackground(GoodFunctions.getRedToGreen(1));
            else
                jm.setBackground(GoodFunctions.getRedToGreen(clm[v].studiedRatio));
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
