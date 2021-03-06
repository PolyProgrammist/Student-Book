package com.company;

import javax.swing.*;
import java.io.Serializable;

public class LessonController implements Serializable {
    private final MainGUI mainGUI;
    private final Profile profile;
    boolean menuCreated = false;
    int nowLessonID = -1;

    public LessonController(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
        profile = mainGUI.getProfile();
    }

    void chooseLessonMenuWorker(JMenu jm, ChooseLessonMenu[] clm, int v) {
        if (!menuCreated) {
            jm.setText(clm[v].name);
            jm.setOpaque(true);
        }
        double studiedRatio = 0;
        if (clm[v].children != null) {
            for (int i = 0; i < clm[v].children.length; i++) {
                int u = clm[v].children[i];
                if (clm[u].children == null || clm[u].children.length == 0)
                    studiedRatio = addThisItemLessonOrNotEnabledTopic(jm, clm, studiedRatio, i, u);
                else
                    studiedRatio = addThisTopicContainer(jm, clm, studiedRatio, i, u);
            }
        }
        clm[v].studiedRatio = studiedRatio / (double) clm[v].children.length;
        if (profile.getProfileName() != null && v != 0) {
            if (clm[v].children == null)
                jm.setBackground(GoodFunctions.getRedToGreen(1));
            else
                jm.setBackground(GoodFunctions.getRedToGreen(clm[v].studiedRatio));
        }
    }
    double addThisItemLessonOrNotEnabledTopic(JMenu jm, ChooseLessonMenu[] clm, double studiedRatio, int i, int u) {
        JMenuItem jmi = menuCreated ? jm.getItem(i) : new JMenuItem(clm[u].name);
        jmi.setOpaque(true);
        if (clm[u].lesson)
            studiedRatio = addThisLessonToMenu(clm[u], studiedRatio, jmi);
        else
            studiedRatio = addThisNotEnabledTopic(studiedRatio, jmi);
        if (!menuCreated)
            jm.add(jmi);
        return studiedRatio;
    }
    double addThisTopicContainer(JMenu jm, ChooseLessonMenu[] clm, double studiedRatio, int i, int u) {
        JMenu addinJM;
        if (!menuCreated) {
            addinJM = new JMenu();
            jm.add(addinJM);
        }
        addinJM = (JMenu) jm.getMenuComponent(i);
        chooseLessonMenuWorker(addinJM, clm, u);
        studiedRatio += (clm[u].children == null) ? 1 : clm[u].studiedRatio;
        return studiedRatio;
    }
    double addThisNotEnabledTopic(double studiedRatio, JMenuItem jmi) {
        if (profile.getProfileName() != null)
            jmi.setBackground(GoodFunctions.getRedToGreen(1));
        jmi.setEnabled(false);
        studiedRatio += 1;
        return studiedRatio;
    }
    double addThisLessonToMenu(final ChooseLessonMenu chooseLessonMenu, double studiedRatio, JMenuItem jmi) {
        boolean done = profile.getProfileName() != null && profile.getHaveStudied()[chooseLessonMenu.lessonID];
        if (profile.getProfileName() != null)
            jmi.setBackground(GoodFunctions.getRedToGreen(done ? 1 : 0));
        studiedRatio += done ? 1 : 0;
        if (!menuCreated)
            jmi.addActionListener(e -> addLesson(chooseLessonMenu.lessonID));
        return studiedRatio;
    }
    void addLessonMaterial() {
        if (nowLessonID == -1) {
            mainGUI.addDefaultOnLessonPane();
            return;
        }
        Reader rd = new Reader(PathConstants.FL + PathConstants.LESSONS_WAY, Main.lessonFileName[nowLessonID] + PathConstants.LESSON_EXTENSION);
        mainGUI.addLessonOnLPane(rd.nextTextFile());
        rd.closeReader();
    }
    void addLesson(int lid) {
        profile.saveAdmin(nowLessonID);
        nowLessonID = lid;
        addLessonMaterial();
        mainGUI.getSomeProfileHandler().fillStudied();
        mainGUI.addTests(lid);
    }

    public void menuCreatedTrue() {
        this.menuCreated = true;
    }
    public int getNowLessonID() {
        return nowLessonID;
    }
}