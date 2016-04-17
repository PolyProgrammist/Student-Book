package com.company;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
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

    public static Color getRedToGreen(double d){//0 - red, 1 - green
        final int r = 100;
        final int g = 100;
        final int b = 100;
        final int mx = 255;
        //Color c = new Color(255, 100, 100);
        return new Color ((int)(r + (1 - d) * (mx - r)), (int)(g + d * (mx - g)), b);
    }

    public static void graphPrinter(int[][] connections){
        System.out.println("<Connections>");
        for (int i = 0; i < connections.length; i++){
            System.out.print(i);
            System.out.print(" :  ");
            for (int j = 0; j < connections[i].length; j++){
                System.out.print(connections[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("</Connections>");
    }

    public static String[] addElementToArray(String[] array, String element) {
        String[] res = new String[array.length + 1];
        for (int i = 0; i < array.length; i++)
            res[i] =  array[i];
        res[array.length] = element;
        return res;
    }

    public static int getPos(String[] profiles, String delName) {
        for (int i = 0; i < profiles.length; i++)
            if (profiles[i] == delName)
                return i;
        return -1;
    }

    public static String[] eraseElementInArray(String[] profiles, int pos) {
        String[] res = new String[profiles.length - 1];
        for (int i = 0; i < profiles.length; i++)
            if (i != pos)
                res[(i < pos) ? i :i - 1] = profiles[i];
        return res;
    }
}
