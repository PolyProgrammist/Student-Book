package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Shuffler {
    public final int shuffleSeed;
    public final int size;
    public final int[] shuf;
    public final int[] antishuf;
    final String ins;

    public Shuffler(String ins, int shuffleSeed) {
        this.ins = ins;
        size = this.ins.length();
        this.shuffleSeed = shuffleSeed;
        shuf = shuffle();
        antishuf = antishuffle();
    }

    private int[] antishuffle() {
        int[] res = new int[size];
        for (int i = 0; i < size; i++)
            res[shuf[i]] = i;
        return res;
    }
    private int[] shuffle() {
        ArrayList<Integer> res = aliOrder(size);
        Collections.shuffle(res, new Random(shuffleSeed));
        int[] result = new int[size];
        for (int i = 0; i < size; i++)
            result[i] = res.get(i);
        return result;
    }
    private static ArrayList<Integer> aliOrder(int sz) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        for (int i = 0; i < sz; i++)
            res.add(i);
        return res;
    }

    public String shuffled(){ return somefled(true); }
    public String unshuffled(){ return somefled(false); }

    private String somefled(boolean did) {
        String res = "";
        for (int i = 0; i < size; i++)
            res += ins.charAt((did ? shuf : antishuf)[i]);
        return res;
    }
}
