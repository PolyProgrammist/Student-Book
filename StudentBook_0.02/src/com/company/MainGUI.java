package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    JLabel haveStudiedLabel;
    JCheckBox haveStudiedCheckBox;
    JPanel haveStudiedPane;

    JPanel panelForTestPanels;

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
    private void addFrameComponents(){
        frame.setJMenuBar(mainMenuBar);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(haveStudiedPane);
        frame.add(mainLessonTextPane);
        frame.add(panelForTestPanels);
    }
    private void prepareFrameComponents() {
        prepareMenuBar();
        prepareStudiedIndicatorPanel();
        prepareLessonMatherialPanel();
        prepareToolBar();
        prepareTestPanels();
        lessonController.addLesson(-1);
    }

    private void prepareStudiedIndicatorPanel() {
        haveStudiedLabel = new JLabel();
        haveStudiedLabel.setOpaque(true);

        haveStudiedCheckBox = new JCheckBox();
        haveStudiedCheckBox.setOpaque(true);
        haveStudiedCheckBox.addActionListener(e -> someProfileHandler.lessonStudiedChange(haveStudiedCheckBox.isSelected()));

        haveStudiedPane = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        haveStudiedPane.add(haveStudiedCheckBox);
        haveStudiedPane.add(haveStudiedLabel);
    }
    private void prepareLessonMatherialPanel() {
        mainLessonTextPane = new JTextPane();
        Font now = mainLessonTextPane.getFont();
        mainLessonTextPane.setFont(new Font(now.getName(), now.getStyle(), 18));
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
    private void prepareTestPanels() {
        panelForTestPanels = new JPanel();
        panelForTestPanels.setLayout(new BoxLayout(panelForTestPanels, BoxLayout.Y_AXIS));
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

    void addTests(int lid){
        panelForTestPanels.removeAll();
        if (lid == -1)
            return;
        for (int tid : Main.lesToTests[lid])
            panelForTestPanels.add(new TestPanel(tid));
    }

    public void addLessonOnLPane(String lesson) {
        mainLessonTextPane.setText(lesson);
    }
    public void addDefaultOnLessonPane() {
        mainLessonTextPane.setText("Your ad could be here");
    }
    public void fillStudied(boolean st) {
        boolean needToShow = profile.getProfileName() != null && lessonController.getNowLessonID() != -1;
        haveStudiedPane.setVisible(needToShow);
        if (needToShow) {
            profile.getHaveStudied()[lessonController.getNowLessonID()] = st;
            haveStudiedCheckBox.setSelected(st);
            haveStudiedLabel.setText(st ? "Studied" : "Not Studied");
            Color c = GoodFunctions.getRedToGreen(st ? 1 : 0);
            haveStudiedLabel.setBackground(c);
            haveStudiedCheckBox.setBackground(c);
            haveStudiedPane.setBackground(c);
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
