package com.company;

import javax.swing.*;
import java.io.Serializable;

public class SomeProfileHandler implements Serializable {
    private final MainGUI mainGUI;
    private final Profile profile;

    public SomeProfileHandler(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
        profile = mainGUI.getProfile();
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
        if (delName.equals(profile.admin)){
            doNotDeleteAdmin();
            return;
        }
        if (!enterPassword(delName))
            return;
        int pos = GoodFunctions.getPos(Main.profiles, delName);
        mainGUI.removeFromProfilesJMenu(pos);
        Main.profiles = GoodFunctions.eraseElementInArray(Main.profiles, pos);
        Main.encryptedPasswords = GoodFunctions.eraseElementInArray(Main.encryptedPasswords, pos);
        profile.updateStringArrayFIle(PathConstants.FL + PathConstants.PROF_INFO_WAY + PathConstants.NOT_PASSWORDS_FILE_NAME, Main.encryptedPasswords);
        profile.updateStringArrayFIle(PathConstants.FL + PathConstants.PROF_INFO_WAY + PathConstants.PROFILES_FILE_NAME, Main.profiles);
        profile.deleteProfileFile(delName);
        if (delName.equals(profile.getProfileName()))
            nextProfile(null);
    }

    private void doNotDeleteAdmin() {
        JOptionPane.showMessageDialog(mainGUI.getFrame(), "Do not delete admin");
    }

    void addNewUserClick() {
        String newName = JOptionPane.showInputDialog(mainGUI.getFrame(), "Enter the name");
        if (newName == null)
            return;
        if (profile.profileExists(newName)) {
            JOptionPane.showMessageDialog(mainGUI.getFrame(), "This profile is already exist", "Seriously, are you testing?", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mainGUI.addToProfilesJMenu(newName);
        Main.profiles = GoodFunctions.addElementToArray(Main.profiles, newName);
        Main.encryptedPasswords = GoodFunctions.addElementToArray(Main.encryptedPasswords, Password.encrypt("0"));
        profile.updateStringArrayFIle(PathConstants.FL + PathConstants.PROF_INFO_WAY + PathConstants.NOT_PASSWORDS_FILE_NAME, Main.encryptedPasswords);
        profile.updateStringArrayFIle(PathConstants.FL + PathConstants.PROF_INFO_WAY + PathConstants.PROFILES_FILE_NAME, Main.profiles);
        profile.createNewProfileFile(newName);
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
        profile.setStudiedChanged(true);
        profile.changeHaveStudiedFromOneEdition(done);
        changeHaveStudiedInformation();
    }
    void changeHaveStudiedInformation() {
        mainGUI.doSomethingToShowIfCanGetRelevantLesson(mainGUI.getNowLessonID());
        fillStudied();
        mainGUI.updateMainMenuBar();
    }
    void fillStudied() {
        mainGUI.fillStudied(mainGUI.getNowLessonID() != -1 && profile.getHaveStudied() != null && profile.getHaveStudied()[mainGUI.getNowLessonID()]);
    }
    void nextProfile(String name) {
        if (profile.getProfileName() != null && name != null && profile.getProfileName().equals(name))
            return;
        if (profile.changeStudentDialog() && enterPassword(name)) {
            profile.setProfileName(name);
            mainGUI.visibleRandomLesson(name != null);
            profile.setStudiedChanged(false);
            if (profile.getProfileName() != null)
                profile.refreshHaveStudiedNewUser();
            mainGUI.showNowProfile();
            changeHaveStudiedInformation();
            mainGUI.addMenuProfSettings();
            if (name != null)
                mainGUI.setEditableLesson(name.equals(profile.admin));
        }
    }

    public boolean enterPassword(String name) {
        int profID = GoodFunctions.getPos(Main.profiles, name);
        if (profID == -1)
            return true;
        if (Main.encryptedPasswords[profID].equals(Password.encrypt("0")))
            return true;
        while(true){
            String inp = inputPassword(name);
            if (inp == null)
                return false;
            if (inp.equals(Password.decrypt(Main.encryptedPasswords[profID])))
                return true;
            else
                badPassword();
        }
    }
    private void badPassword() {
        JOptionPane.showMessageDialog(mainGUI.getFrame(), "Wrong password");
    }
    public String inputPassword(String name){
        JPasswordField pf = new JPasswordField(15);
        int action = JOptionPane.showConfirmDialog(mainGUI.getFrame(), pf, String.format("Enter Password for %s", name), JOptionPane.OK_CANCEL_OPTION);
        if (action == JOptionPane.CANCEL_OPTION)
            return null;
        return String.valueOf(pf.getPassword());
    }
}

interface IUsersMethodsHandler {
    void handle();
}