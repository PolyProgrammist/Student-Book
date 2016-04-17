package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by vadim on 28.03.2016.
 */
public class MainGUI extends JFrame{
    JFrame frame;
    JMenuBar mainMenuBar;
    JTextPane mainLessonTextPane;
    JButton randomLesson;
    JMenu topicsMenu = new JMenu(), classesMenu = new JMenu();
    JToolBar jtb;

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

    int nowLessonID;

    private void prepareFrame(){
        frame = new JFrame("StudentBook");
        prepareFrameComponents();
        addFrameComponents();
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
    private void addFrameComponents() {
        frame.setJMenuBar(mainMenuBar);
        frame.setLayout(new GridBagLayout());

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
    }

    public MainGUI(){
        prepareFrame();
    }

    private void prepareFrameComponents() {
        prepareMenuBar();
        prepareStudiedIndicatorPanel();
        prepareLessonMatherialPanel();
        prepareToolBar();
        prepareTestPanel();
        addLesson(-1);
    }

    private void prepareStudiedIndicatorPanel() {
        tmpLabel = new JLabel();
        tmpLabel.setOpaque(true);

        tmpCheckBox = new JCheckBox();
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
    }
    private void prepareLessonMatherialPanel() {
        mainLessonTextPane = new JTextPane();
        mainLessonTextPane.setEditable(false);
    }
    private void prepareToolBar() {
        jtb = new JToolBar();
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
    }
    private void prepareTestPanel() {
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
    }
    private void prepareMenuBar() {
        mainMenuBar  = new JMenuBar();
        mainMenuBar.add(topicsMenu);
        mainMenuBar.add(classesMenu);
        updateMainMenuBar();
        JMenu profjm = profilesJMenu(Main.profiles);
        mainMenuBar.add(profjm);
    }


    private void saveProfileChanges() {
        if (profileName == null)
            return;
        try {
            PrintWriter writer = new PrintWriter(PathConstants.FL + PathConstants.PROF_INFO_WAY + profileName + ".ai");
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
    void changeHaveStudiedFromOneEdition(int lid, boolean done){
        haveStudied[nowLessonID] = done;
        HaveStudiedCalc hsc = new HaveStudiedCalc(haveStudied, Main.connections, Main.anticonnections);
        haveStudied = hsc.getChange(nowLessonID, done);
    }

    public void updateMainMenuBar(){
        mainMenuBar.remove(topicsMenu);
        mainMenuBar.remove(classesMenu);
        topicsMenu = addChooseLessonMenu(Main.topics, 0);
        classesMenu = addChooseLessonMenu(Main.classes, 0);
        mainMenuBar.add(classesMenu, 0);
        mainMenuBar.add(topicsMenu, 0);
    }
    private void refreshHaveStudiedNewUser(){
        Reader rd = new Reader(PathConstants.FL + PathConstants.PROF_INFO_WAY, String.format("%s.ai", profileName));
        haveStudied_fileDataInitial = GoodFunctions.intArrayToBooleanArray(rd.nextIntArray(), Main.lessonFileName.length);
        HaveStudiedCalc hsc = new HaveStudiedCalc(haveStudied_fileDataInitial, Main.connections, Main.anticonnections);
        haveStudied = hsc.getFullFinished();
        rd.closeReader();
    }
    private void nextProfile(String name){
        if (profileName != null && name != null && profileName.equals(name))
            return;
        if (changeStudentDialog()) {
            profileName = name;
            randomLesson.setVisible(name != null);
            studiedChanged = false;
            if (profileName != null)
                refreshHaveStudiedNewUser();
            mainMenuBar.getMenu(2).setText(profileName == null ? "Profiles" : profileName);
            fillStudied();
            updateMainMenuBar();
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
                                addLesson(clm[u].lessonID);
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

    private void addJMenuProfileHandling(JMenu res, String s, IUsersMethodsHandler hand){
        JMenuItem jmi = new JMenuItem(s);
        jmi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hand.handle();
            }
        });
        res.add(jmi);
    }
    private void deleteUserClick() {
        String[] possibilities = Main.profiles;
        String delName = (String) JOptionPane.showInputDialog(frame, "Choose the name", "Name deleter", JOptionPane.PLAIN_MESSAGE, null, Main.profiles, "...");
        if (delName == null)
            return;
        int pos = GoodFunctions.getPos(Main.profiles, delName);
        mainMenuBar.getMenu(2).remove(pos);
        Main.profiles = GoodFunctions.eraseElementInArray(Main.profiles, pos);
        updateProfilesFile();
        deleteProfileFile(delName);
        if (delName.equals(profileName))
            nextProfile(null);
    }
    private void addNewUserClick() {
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
    private JMenu profilesJMenu(String[] profiles){
        JMenu res = new JMenu("Profiles");
        for (int i = 0; i < profiles.length; i++){
            JMenuItem jmi = new JMenuItem(profiles[i]);
            jmi.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    nextProfile(jmi.getText());
                }
            });
            res.add(jmi);
        }
        addJMenuProfileHandling(res, "Add Profile", this::addNewUserClick);
        if (profiles.length > 0)
            addJMenuProfileHandling(res, "Delete Profile", this::deleteUserClick);
        return res;
    }
    private void lessonStudiedChange(boolean done) {
        studiedChanged = true;
        changeHaveStudiedFromOneEdition(nowLessonID, done);
        fillStudied();
        updateMainMenuBar();
    }
    private void fillStudied() {
        fillStudied(nowLessonID != -1 && haveStudied[nowLessonID]);
    }
    private void fillStudied(boolean st){
        boolean needToShow = profileName != null && nowLessonID != -1;
        tmpPane.setVisible(needToShow);
        if (needToShow) {
            haveStudied[nowLessonID] = st;
            tmpCheckBox.setSelected(st);
            tmpLabel.setText(st ? "Studied" : "Not Studied");
            Color c = GoodFunctions.getRedToGreen(st ? 1 : 0);
            tmpLabel.setBackground(c);
            tmpCheckBox.setBackground(c);
            tmpPane.setBackground(c);
        }
    }
    private void addLessonMatherial(){
        if (nowLessonID == -1) {
            mainLessonTextPane.setText("Your ad could be here");
            return;
        }
        Reader rd = new Reader(PathConstants.FL + PathConstants.LESSONS_WAY, Main.lessonFileName[nowLessonID] + PathConstants.LESSON_EXTENSION);
        mainLessonTextPane.setText(rd.nextTextFile());
        rd.closeReader();
    }
    private void addLesson(int lid){
        nowLessonID = lid;
        addLessonMatherial();
        fillStudied();
        addTests();
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
    private void addTests() {
        if (nowLessonID == -1)
            return;
        int lid = nowLessonID;
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
    private boolean relevantToLearn(int lid) {
        for (int i = 0; i < Main.anticonnections[lid].length; i++)
            if (!haveStudied[Main.anticonnections[lid][i]])
                return false;
        return true;
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
            if (!haveStudied[i] && i != nowLessonID && relevantToLearn(i))
                res++;
        if (res == 0)
            return;
        Random rnd = new Random();
        int now = Math.abs(rnd.nextInt()) % res;
        int tmp = -1, gt = -1;
        for (int i = 0; i < haveStudied.length; i++)
            if (!haveStudied[i] && i != nowLessonID && relevantToLearn(i)) {
                tmp++;
                if (tmp == now){
                    gt = i;
                    break;
                }
            }
        addLesson(gt);
    }
    private void createNewProfileFile(String newName) {
        String destination = PathConstants.FL + PathConstants.PROF_INFO_WAY + newName + ".ai";
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
            PrintWriter writer = new PrintWriter(PathConstants.FL + PathConstants.PROF_INFO_WAY + PathConstants.PROFILES_FILE_NAME);
            writer.println(Main.profiles.length);
            for (int i = 0; i < Main.profiles.length; i++)
                writer.println(Main.profiles[i]);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void deleteProfileFile(String delName) {
        File file = new File(PathConstants.FL + PathConstants.PROF_INFO_WAY + delName + ".ai");
        file.delete();
    }

    public static void launch(){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                new MainGUI();
            }
        });
    }
}

interface IUsersMethodsHandler {
    void handle();
}