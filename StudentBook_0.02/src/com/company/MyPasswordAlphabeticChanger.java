package com.company;

public class MyPasswordAlphabeticChanger extends AlphabeticChanger{
    static final int lets = 26;
    static final int digs = 10;

    public MyPasswordAlphabeticChanger(int shuffleSeed) {
        super(alph(), shuffleSeed, MyPasswordAlphabeticChanger::getID);
    }

    public static int getID(char c){
        return Character.isDigit(c) ? c - '0' : (Character.isLowerCase(c) ? c - 'a' : (Character.isUpperCase(c)  ? c - 'A' + lets : -1)) + digs;
    }
    private static String alph(){
        String res = "";
        for (int i = 0; i < digs; i++)
            res += (char) (i + '0');
        for (int i = 0; i < lets; i++)
            res += (char) (i + 'a');
        for (int i = 0; i < lets; i++)
            res += (char) (i + 'A');
        return res;
    }
}
