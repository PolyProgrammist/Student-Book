package com.company;

import javafx.util.Pair;

import javax.swing.*;
import java.io.*;
import java.util.Random;

public class Profile implements Serializable {
    public final String admin = "�������������";
    private final MainGUI mainGUI;
    String profileName;
    boolean[] haveStudied_fileDataInitial;
    boolean[] haveStudied;
    boolean studiedChanged;

    public Profile(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
    }

    void saveProfileChanges() {
        if (profileName == null)
            return;
        try {
            PrintWriter writer = new PrintWriter(PathConstants.FL + PathConstants.PROF_INFO_WAY + profileName + ".ai");
            int sz = 0;
            for (boolean aHaveStudied : haveStudied) sz += aHaveStudied ? 1 : 0;
            writer.println(sz);
            for (int i = 0; i < haveStudied.length; i++)
                if (haveStudied[i])
                    writer.println(i);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    boolean changeStudentDialog() {
        saveAdmin(mainGUI.lessonController.getNowLessonID());
        if (profileName == null || !studiedChanged)
            return true;
        int opt = JOptionPane.showConfirmDialog(mainGUI.getFrame(), "Do you want to save changes?", String.format("So, save %s's data?", profileName), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opt == JOptionPane.CANCEL_OPTION)
            return false;
        if (opt == JOptionPane.YES_OPTION)
            saveProfileChanges();
        return true;
    }
    boolean profileExists(String newName) {
        for (int i = 0; i < Main.profiles.length; i++)
            if (Main.profiles[i].equals(newName))
                return true;
        return false;
    }
    void updateStringArrayFIle(String filePath, String[] ar) {
        try {
            PrintWriter writer = new PrintWriter(filePath);
            writer.println(ar.length);
            for (String anAr : ar) writer.println(anAr);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    void deleteProfileFile(String delName) {
        File file = new File(PathConstants.FL + PathConstants.PROF_INFO_WAY + delName + ".ai");
        file.delete();
    }
    void createNewProfileFile(String newName) {
        String destination = PathConstants.FL + PathConstants.PROF_INFO_WAY + newName + ".ai";
        File f = new File(destination);
        try {
            f.createNewFile();
            PrintWriter writer = new PrintWriter(f);
            writer.println(0);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void changeHaveStudiedFromOneEdition(boolean done) {
        haveStudied[mainGUI.getNowLessonID()] = done;
        HaveStudiedCalc hsc = new HaveStudiedCalc(haveStudied, Main.connections, Main.anticonnections);
        haveStudied = hsc.getChange(mainGUI.getNowLessonID(), done);
    }
    void refreshHaveStudiedNewUser() {
        Reader rd = new Reader(PathConstants.FL + PathConstants.PROF_INFO_WAY, String.format("%s.ai", profileName));
        haveStudied_fileDataInitial = GoodFunctions.intArrayToBooleanArray(rd.nextIntArray(), Main.lessonFileName.length);
        HaveStudiedCalc hsc = new HaveStudiedCalc(haveStudied_fileDataInitial, Main.connections, Main.anticonnections);
        haveStudied = hsc.getFullFinished();
        rd.closeReader();
    }

    boolean relevantToLearn(int lid) {
        for (int ac : Main.anticonnections[lid])
            if (!haveStudied[ac])
                return false;
        return true;
    }
    int getRandomRelevantLesson(int lid){
        int res = 0;
        for (int i = 0; i < haveStudied.length; i++)
            if (!haveStudied[i] && i != lid && relevantToLearn(i))
                res++;
        if (res == 0)
            return -1;
        Random rnd = new Random();
        int now = Math.abs(rnd.nextInt()) % res;
        int tmp = -1, gt = -1;
        for (int i = 0; i < haveStudied.length; i++)
            if (!haveStudied[i] && i != lid && relevantToLearn(i)) {
                tmp++;
                if (tmp == now){
                    gt = i;
                    break;
                }
            }
        return gt;
    }

    public String getProfileName() {
        return profileName;
    }
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
    public boolean[] getHaveStudied() {
        return haveStudied;
    }
    public void setStudiedChanged(boolean studiedChanged) {
        this.studiedChanged = studiedChanged;
    }

    private void changePassword(String ins) {
        int pid = GoodFunctions.getPos(Main.profiles, profileName);
        Main.encryptedPasswords[pid] = ins;
        updateStringArrayFIle(PathConstants.FL + PathConstants.PROF_INFO_WAY + PathConstants.NOT_PASSWORDS_FILE_NAME, Main.encryptedPasswords);
    }
    public void changePasswordDialogs() {
        if (mainGUI.someProfileHandler.enterPassword(profileName)) {
            while (true){
                Pair<String, String> inp = mainGUI.inputNewPassword();
                if (inp == null)
                    return;
                if (!inp.getKey().equals(inp.getValue())){
                    mainGUI.differentPasswords();
                    continue;
                }
                String ins = inp.getValue();
                if (!ins.equals("0")) {
                    if (ins.length() < 4) {
                        mainGUI.shortPassword();
                        continue;
                    }
                    if (!Password.checkGood(ins)) {
                        mainGUI.badSymbols();
                        continue;
                    }
                }
                changePassword(Password.encrypt(ins));
                return;
            }
        }
    }

    public void saveAdmin(int lid) {
        if (profileName == null || !profileName.equals(admin) || lid == -1)
            return;
        try {
            PrintWriter writer = new PrintWriter(PathConstants.FL + PathConstants.LESSONS_WAY + Main.lessonFileName[lid] + PathConstants.LESSON_EXTENSION);
            writer.print(mainGUI.mainLessonTextPane.getText());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}