package com.company;

import java.util.Vector;

/**
 * Created by vadim on 01.04.2016.
 */
public class GoodFunctions {
    public static boolean[] intArrayToBooleanArray(int[] ints, int n) {
        boolean[] res = new boolean[n];
        for (int i = 0; i < n; i++)
            res[i] = false;
        for (int i = 0; i < ints.length; i++)
            res[ints[i]] = true;
        return res;
    }

    public static int[][] makeTransParentGraph(int[][] connections) {
        int[] count = new int[connections.length];
        for (int i = 0; i < connections.length; i++)
            for (int j = 0; j < connections[i].length; j++)
                count[connections[i][j]]++;
        int[][] res = new int[connections.length][];
        System.out.println(res.length);
        for (int i = 0; i < connections.length; i++)
            res[i] = new int[count[i]];
        int[] now = new int[connections.length];
        for (int i = 0; i < connections.length; i++)
            for (int j = 0; j < connections[i].length; j++){
                int t = connections[i][j];
                res[t][now[t]] = i;
                now[t]++;
            }
        return res;
    }
}
