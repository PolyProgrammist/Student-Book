package com.company;

import javax.swing.*;
import java.io.Serializable;

public class SomeProfileHandler implements Serializable {
    private final MainGUI mainGUI;

    public SomeProfileHandler(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }
    void addJMenuProfileHandling(JMenu res, String s, IUsersMethodsHandler hand) {
        JMenuItem jmi = new JMenuItem(s);
        jmi.addActionListener(e -> hand.handle());
        res.add(jmi);
    }
    void deleteUserClick() {
        String delName = (String) JOptionPane.showInputDialog(mainGUI.getFrame(), "Choose the name", "Name deleter", JOptionPane.PLAIN_MESSAGE, null, Main.profiles, "...");
        if (delName == null)
            return;
        int pos = GoodFunctions.getPos(Main.profiles, delName);
        mainGUI.removeFromProfilesJMenu(pos);
        Main.profiles = GoodFunctions.eraseElementInArray(Main.profiles, pos);
        mainGUI.getProfile().updateProfilesFile();
        mainGUI.getProfile().deleteProfileFile(delName);
        if (delName.equals(mainGUI.getProfile().getProfileName()))
            nextProfile(null);
    }
    void addNewUserClick() {
        String newName = JOptionPane.showInputDialog(mainGUI.getFrame(), "Enter the name");
        if (newName == null)
            return;
        if (mainGUI.getProfile().profileExists(newName)) {
            JOptionPane.showMessageDialog(mainGUI.getFrame(), "This profile is already exist", "Seriously, are you testing?", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mainGUI.addToProfilesJMenu(newName);
        Main.profiles = GoodFunctions.addElementToArray(Main.profiles, newName);
        mainGUI.getProfile().updateProfilesFile();
        mainGUI.getProfile().createNewProfileFile(newName);
        nextProfile(newName);
    }
    JMenu profilesJMenu(String[] profiles) {
        JMenu res = new JMenu("Profiles");
        for (String profile : profiles) {
            JMenuItem jmi = new JMenuItem(profile);
            jmi.addActionListener(e -> nextProfile(jmi.getText()));
            res.add(jmi);
        }
        addJMenuProfileHandling(res, "Add Profile", this::addNewUserClick);
        if (profiles.length > 0)
            addJMenuProfileHandling(res, "Delete Profile", this::deleteUserClick);
        return res;
    }
    void lessonStudiedChange(boolean done) {
        mainGUI.getProfile().setStudiedChanged(true);
        mainGUI.getProfile().changeHaveStudiedFromOneEdition(done);
        changeHaveStudiedInformation();
    }
    void changeHaveStudiedInformation() {
        mainGUI.doSomethingToShowIfCanGetRelevantLesson(mainGUI.getNowLessonID());
        fillStudied();
        mainGUI.updateMainMenuBar();
    }
    void fillStudied() {
        mainGUI.fillStudied(mainGUI.getNowLessonID() != -1 && mainGUI.getProfile().getHaveStudied() != null && mainGUI.getProfile().getHaveStudied()[mainGUI.getNowLessonID()]);
    }
    void nextProfile(String name) {
        if (mainGUI.getProfile().getProfileName() != null && name != null && mainGUI.getProfile().getProfileName().equals(name))
            return;
        if (mainGUI.getProfile().changeStudentDialog()) {
            mainGUI.getProfile().setProfileName(name);
            mainGUI.visibleRandomLesson(name != null);
            mainGUI.getProfile().setStudiedChanged(false);
            if (mainGUI.getProfile().getProfileName() != null)
                mainGUI.getProfile().refreshHaveStudiedNewUser();
            mainGUI.showNowProfile();
            changeHaveStudiedInformation();
        }
    }
}

interface IUsersMethodsHandler {
    void handle();
}