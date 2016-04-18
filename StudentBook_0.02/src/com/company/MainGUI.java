package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainGUI extends JFrame{
    final Profile profile = new Profile(this);
    final LessonController lessonController = new LessonController(this);
    private final SomeProfileHandler someProfileHandler = new SomeProfileHandler(this);
    JFrame frame;

    JTextPane mainLessonTextPane;

    JButton randomLesson;
    JToolBar jtb;

    JMenuBar mainMenuBar;
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

    public static void launch(){
        SwingUtilities.invokeLater(MainGUI::new);
    }

    public MainGUI(){
        prepareFrame();
    }

    private void prepareFrame(){
        frame = new JFrame("StudentBook");
        prepareFrameComponents();
        addFrameComponents();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (profile.changeStudentDialog())
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
    private void prepareFrameComponents() {
        prepareMenuBar();
        prepareStudiedIndicatorPanel();
        prepareLessonMatherialPanel();
        prepareToolBar();
        prepareTestPanel();
        lessonController.addLesson(-1);
    }

    private void prepareStudiedIndicatorPanel() {
        tmpLabel = new JLabel();
        tmpLabel.setOpaque(true);

        tmpCheckBox = new JCheckBox();
        tmpCheckBox.setOpaque(true);
        tmpCheckBox.addActionListener(e -> someProfileHandler.lessonStudiedChange(tmpCheckBox.isSelected()));

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
        randomLesson.addActionListener(e -> lessonController.addLesson(profile.getRandomRelevantLesson(lessonController.getNowLessonID())));
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
        topicsMenu = new JMenu();
        classesMenu = new JMenu();
        updateMainMenuBar();
        mainMenuBar.add(topicsMenu);
        mainMenuBar.add(classesMenu);
        JMenu profjm = someProfileHandler.profilesJMenu(Main.profiles);
        mainMenuBar.add(profjm);
        lessonController.menuCreatedTrue();
    }

    public void updateMainMenuBar(){
        lessonController.chooseLessonMenuWorker(topicsMenu, Main.topics, 0);
        lessonController.chooseLessonMenuWorker(classesMenu, Main.classes, 0);
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
    public void addTests(int lid) {
        if (lid == -1)
            return;
        st = SimpleTest.getTest(lid);
        if (st != null){
            testPanel.setVisible(true);
            testTaskTextLabel.setText(st.task);
            testThisProblemLabel.setText(st.expression);
            testAnswerField.setText("");
            testAnswerButton.addActionListener(e -> checkAnswer(lid));
            testAnswerField.addActionListener(e -> checkAnswer(lid));
        }
        else
            testPanel.setVisible(false);
    }

    public void addLessonOnLPane(int nowLessonID) {
        Reader rd = new Reader(PathConstants.FL + PathConstants.LESSONS_WAY, Main.lessonFileName[nowLessonID] + PathConstants.LESSON_EXTENSION);
        mainLessonTextPane.setText(rd.nextTextFile());
        rd.closeReader();
    }
    public void addDefaultOnLessonPane() {
        mainLessonTextPane.setText("Your ad could be here");
    }
    public void fillStudied(boolean st) {
        boolean needToShow = profile.getProfileName() != null && lessonController.getNowLessonID() != -1;
        tmpPane.setVisible(needToShow);
        if (needToShow) {
            profile.getHaveStudied()[lessonController.getNowLessonID()] = st;
            tmpCheckBox.setSelected(st);
            tmpLabel.setText(st ? "Studied" : "Not Studied");
            Color c = GoodFunctions.getRedToGreen(st ? 1 : 0);
            tmpLabel.setBackground(c);
            tmpCheckBox.setBackground(c);
            tmpPane.setBackground(c);
        }
    }
    public void visibleRandomLesson(boolean can) {
        randomLesson.setVisible(can);
    }
    public void showNowProfile() {
        getProfilesJMenuComponent().setText(profile.getProfileName() == null ? "Profiles" : profile.getProfileName());
    }
    public void doSomethingToShowIfCanGetRelevantLesson(int lid) {
        randomLesson.setEnabled(profile.getRandomRelevantLesson(lid) != -1);
    }
    public void addToProfilesJMenu(String newName) {
        JMenuItem jmiNewUser = new JMenuItem(newName);
        jmiNewUser.addActionListener(e -> someProfileHandler.nextProfile(newName));
        getProfilesJMenuComponent().insert(jmiNewUser, Main.profiles.length);
    }
    public void removeFromProfilesJMenu(int pos) {
        getProfilesJMenuComponent().remove(pos);
    }

    private JMenu getProfilesJMenuComponent() {
        return mainMenuBar.getMenu(2);
    }

    public int getNowLessonID() {
        return lessonController.getNowLessonID();
    }
    public JFrame getFrame() {
        return frame;
    }
    public Profile getProfile() {
        return profile;
    }
    public SomeProfileHandler getSomeProfileHandler() {
        return someProfileHandler;
    }
}
