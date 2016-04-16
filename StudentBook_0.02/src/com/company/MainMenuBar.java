package com.company;

import javafx.util.Pair;
import org.python.core.PySystemState;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

import static java.lang.System.exit;

/**
 * Created by vadim on 28.03.2016.
 */
public class MainMenuBar extends JFrame{
    JFrame frame;
    JMenuBar mainMenuBar;
    JTextPane mainLessonTextPane;
    JButton randomLesson;
    JMenu topicsMenu = new JMenu(), classesMenu = new JMenu();

    JLabel tmpLabel;
    JPanel tmpPane;
    JCheckBox tmpCheckBox;

    JLabel testTaskTextLabel;
    JLabel testThisProblemLabel;
    JTextField testAnswerField;
    JButton testAnswerButton;
    JPanel testPanel;
    SimpleTest st;

    String profileName = null;
    boolean[] haveStudied_fileDataInitial;
    boolean[] haveStudied;
    boolean studiedChanged = false;

    int nowLessonID = -1;

    public MainMenuBar(){

        frame = new JFrame("StudentBook");

        mainMenuBar  = new JMenuBar();
        mainMenuBar.add(topicsMenu);
        mainMenuBar.add(classesMenu);
        InitializeProfileEnvironment();
        JMenu profjm = profilesJMenu(Main.profiles);
        mainMenuBar.add(profjm);

        frame.setJMenuBar(mainMenuBar);
        frame.setLayout(new GridBagLayout());

        tmpLabel = new JLabel();
        tmpLabel.setText("");
        tmpLabel.setOpaque(true);

        tmpCheckBox = new JCheckBox();
        tmpCheckBox.setVisible(false);
        tmpCheckBox.setOpaque(true);
        tmpCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lessonStudiedChange(tmpCheckBox.isSelected());
            }
        });

        tmpPane = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        tmpPane.add(tmpCheckBox);
        tmpPane.add(tmpLabel);

        mainLessonTextPane = new JTextPane();
        mainLessonTextPane.setText("Your ad could be here");
        mainLessonTextPane.setEditable(false);

        JToolBar jtb = new JToolBar();
        randomLesson = new JButton();
        randomLesson.setText("Подходящий урок");
        randomLesson.setVisible(false);
        randomLesson.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangeLessonToRandom();
            }
        });
        jtb.add(randomLesson);

        testTaskTextLabel = new JLabel("Task");
        testThisProblemLabel = new JLabel("Expression");
        testAnswerField = new JTextField("");
        testAnswerButton = new JButton("Check Answer");
        testPanel = new JPanel(new BorderLayout());
        testPanel.add(testTaskTextLabel, BorderLayout.NORTH);
        testPanel.add(testThisProblemLabel, BorderLayout.CENTER);
        testPanel.add(testAnswerField, BorderLayout.SOUTH);
        testPanel.add(testAnswerButton, BorderLayout.EAST);
        testPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weighty = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;

        gbc.gridy = 0;
        frame.add(jtb, gbc);
        gbc.gridy = 1;
        frame.add(tmpPane, gbc);

        gbc.weighty = 1;
        gbc.gridy = 2;
        frame.add(mainLessonTextPane, gbc);
        gbc.gridy = 3;
        frame.add(testPanel, gbc);


        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (changeStudentDialog())
                    System.exit(0);
            }
        });

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }
    private boolean changeStudentDialog(){
        if (profileName == null || !studiedChanged)
            return true;
        int opt = JOptionPane.showConfirmDialog(frame, "Do you want to save changes?", String.format("So, save %s's data?", profileName), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.CANCEL_OPTION)
            return false;
        if (opt == JOptionPane.YES_OPTION)
            saveProfileChanges();
        return true;
    }
    private void ChangeLessonToRandom(){
        int res = 0;
        for (int i = 0; i < haveStudied.length; i++)
            if (!haveStudied[i] && i != nowLessonID && relevantTolearn(i))
                res++;
        if (res == 0)
            return;
        Random rnd = new Random();
        int now = Math.abs(rnd.nextInt()) % res;
        int tmp = -1;
        int gt = -1;
        for (int i = 0; i < haveStudied.length; i++)
            if (!haveStudied[i] && i != nowLessonID && relevantTolearn(i)) {
                tmp++;
                if (tmp == now){
                    gt = i;
                    break;
                }
            }
        nowLessonID = gt;
        addLesson(gt, haveStudied[gt]);
    }

    private boolean relevantTolearn(int lid) {
        for (int i = 0; i < Main.anticonnections[lid].length; i++)
            if (!haveStudied[Main.anticonnections[lid][i]])
                return false;
        return true;
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
        tmpLabel.setVisible(true);
        tmpCheckBox.setVisible(true);
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
            rd.closeReader();
        }
        updateMainMenuBar();
    }
    private void nextProfile(String name){
        if (name == null || profileName != name && changeStudentDialog()) {
            randomLesson.setVisible(name != null);
            studiedChanged = false;
            profileName = name;
            InitializeProfileEnvironment();
            mainMenuBar.getMenu(2).setText(name == null ? "Profiles" : profileName);
            if (nowLessonID != -1)
                addLesson(nowLessonID, haveStudied[nowLessonID]);
        }
        if (name == null) {
            tmpCheckBox.setVisible(false);
            tmpLabel.setVisible(false);
        }
    }
    private JMenu profilesJMenu(String[] profiles){
        JMenu res = new JMenu("Profiles");
        for (int i = 0; i < profiles.length; i++){
            JMenuItem jmi = new JMenuItem(profiles[i]);
            jmi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
                        nextProfile(jmi.getText());
                    }
                }
            });
            res.add(jmi);
        }
        JMenuItem jmiAdd = new JMenuItem("Add Profile");
        jmiAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newName = JOptionPane.showInputDialog(frame, "Enter the name");
                if (newName == null)
                    return;
                if (profileExists(newName)) {
                    JOptionPane.showMessageDialog(frame, "This profile is already exist", "Seriously, are you testing?", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JMenuItem jmiNewUser = new JMenuItem(newName);
                jmiNewUser.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        nextProfile(newName);
                    }
                });
                mainMenuBar.getMenu(2).insert(jmiNewUser, Main.profiles.length);
                Main.profiles = GoodFunctions.addElementToArray(Main.profiles, newName);
                updateProfilesFile();
                createNewProfileFile(newName);
                nextProfile(newName);
            }
        });
        res.add(jmiAdd);
        if (profiles.length > 0) {
            JMenuItem jmiDelete = new JMenuItem("Delete Profile");
            jmiDelete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String[] possibilities = Main.profiles;
                    String delName = (String)JOptionPane.showInputDialog(frame, "Choose the name", "Name deleter", JOptionPane.PLAIN_MESSAGE, null, Main.profiles, "...");
                    if (delName == null)
                        return;
                    int pos = GoodFunctions.getPos(Main.profiles, delName);
                    mainMenuBar.getMenu(2).remove(pos);
                    Main.profiles = GoodFunctions.eraseElementInArray(Main.profiles, pos);
                    updateProfilesFile();
                    deleteProfileFile(delName);
                    if (delName == profileName)
                        nextProfile(null);
                }
            });
            res.add(jmiDelete);
        }
        return res;
    }
    private void deleteProfileFile(String delName) {
        String delPath = Reader.DEFAULT_PROF_INFO_WAY + delName + ".ai";
        File file = new File(delPath);
        file.delete();
    }
    private void createNewProfileFile(String newName) {
        String destination = Reader.DEFAULT_PROF_INFO_WAY + newName + ".ai";
        File f = new File (destination);
        try {
            f.createNewFile();
            PrintWriter writer = new PrintWriter(f);
            writer.println(0);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean profileExists(String newName) {
        for (int i = 0; i < Main.profiles.length; i++)
            if (Main.profiles[i] == newName)
               return true;
        return false;
    }
    private void updateProfilesFile() {
        try {
            PrintWriter writer = new PrintWriter(Reader.DEFAULT_PROF_INFO_WAY + Reader.DEFAULT_PROFILES_FILE_NAME);
            writer.println(Main.profiles.length);
            for (int i = 0; i < Main.profiles.length; i++)
                writer.println(Main.profiles[i]);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void addLesson(int lid, boolean done){
        String s = Reader.DEFAULT_LESSONS_WAY + Main.lessonFileName[lid] + Reader.LESSON_EXTENSION;
        try {
            File file = new File(s);
            String res = new String();
            Scanner sc = new Scanner(file);
            while (sc.hasNext())
                res += sc.nextLine() + '\n';
            mainLessonTextPane.setText(res);
            sc.close();
        } catch (FileNotFoundException e1) {
            JOptionPane.showMessageDialog(new JPanel(), String.format("Could not open file %s\n%s", s, e1.getMessage()), "addLesson", JOptionPane.ERROR_MESSAGE);
        }
        if (profileName != null) {
            tmpCheckBox.setVisible(true);
            tmpCheckBox.setSelected(done);
            fillStudied(done);
        }
        addTests(lid);
    }
    private void checkAnswer(int lid){
        String usans = testAnswerField.getText();
        if (usans == null || usans.equals(""))
            return;
        if (usans.equals(st.answer)) {
            st = SimpleTest.getTest(lid);
            testThisProblemLabel.setText(st.expression);
            testAnswerField.setText("");
            testAnswerButton.setText("Right");
        }
        else
            testAnswerButton.setText("Wrong");
    }
    private void addTests(int lid) {
        st = SimpleTest.getTest(lid);
        if (st != null){
            testPanel.setVisible(true);
            testTaskTextLabel.setText(st.task);
            testThisProblemLabel.setText(st.expression);
            testAnswerField.setText("");
            testAnswerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkAnswer(lid);
                }
            });
            testAnswerField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkAnswer(lid);
                }
            });
        }
        else
            testPanel.setVisible(false);
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
                                addLesson(nowLessonID, finalDone);
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
