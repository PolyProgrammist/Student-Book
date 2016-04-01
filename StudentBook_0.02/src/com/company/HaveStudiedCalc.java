package com.company;

/**
 * Created by vadim on 01.04.2016.
 */
public class HaveStudiedCalc {
    boolean[] haveStudiedInitial;
    int[][] anticonnections;
    boolean[] used;

    public HaveStudiedCalc(boolean[] haveStudiedInitial, int[][] anticonnections) {
        this.haveStudiedInitial = haveStudiedInitial;
        this.anticonnections = anticonnections;
        used = new boolean[haveStudiedInitial.length];
    }

    public boolean[] getFinished() {
        for (int i = 0; i < haveStudiedInitial.length; i++)
            if (haveStudiedInitial[i] && !used[i])
                DFS(i);
        return used;
    }

    private void DFS(int v) {
        used[v] = true;
        for (int i = 0; i < anticonnections[v].length; i++)
            if (!used[anticonnections[v][i]])
                DFS(anticonnections[v][i]);
    }
}
