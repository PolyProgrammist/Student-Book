package com.company;

public class HaveStudiedCalc {
    boolean[] haveStudiedInitial;
    int[][] anticonnections, connections;
    boolean[] used;

    public HaveStudiedCalc(boolean[] haveStudiedInitial, int[][] connections, int[][] anticonnections) {
        this.haveStudiedInitial = haveStudiedInitial;
        this.connections = connections;
        this.anticonnections = anticonnections;
        used = new boolean[haveStudiedInitial.length];
    }

    public boolean[] getFullFinished() {
        for (int i = 0; i < used.length; i++)
            used[i] = false;
        for (int i = 0; i < haveStudiedInitial.length; i++)
            if (haveStudiedInitial[i] && !used[i])
                DFS(i);
        return used;
    }

    public boolean[] getChange(int v, boolean pos){
        DFSChange(v, pos, (pos ? anticonnections : connections));
        return haveStudiedInitial;
    }

    private void DFSChange(int v, boolean pos, int[][] con){
        haveStudiedInitial[v] = pos;
        for (int i = 0; i < con[v].length; i++)
            if (pos != haveStudiedInitial[con[v][i]])
                DFSChange(con[v][i], pos, con);
    }

    private void DFS(int v) {
        used[v] = true;
        for (int i = 0; i < anticonnections[v].length; i++)
            if (!used[anticonnections[v][i]])
                DFS(anticonnections[v][i]);
    }
}
