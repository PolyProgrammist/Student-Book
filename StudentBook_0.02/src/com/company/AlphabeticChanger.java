package com.company;

public class AlphabeticChanger {
    final String alphabet;
    final int size;
    public final Shuffler shuffler;
    final IDgetter idg;
    static IDgetter defIdg = new IDgetter(){
        @Override
        public int getID(char c) {
            return -1;
        }
    };//strange for a time;

    public AlphabeticChanger(String alphabet, int shuffleSeed, IDgetter idg) {
        this.alphabet = alphabet;
        shuffler = new Shuffler(alphabet, shuffleSeed);
        size = this.alphabet.length();
        this.idg = idg;
    }

    public String changed(String ins){ return somenged(ins, true); }
    public String antichanged(String ins){ return somenged(ins, false); }
    private String somenged(String ins, boolean did){
        String res = "";
        for (int i = 0; i < ins.length(); i++)
            res += alphabet.charAt((did ? shuffler.shuf : shuffler.antishuf)[idg.getID(ins.charAt(i))]);
        return res;
    }
    public boolean checkGood(String ins) {
        for (int i = 0; i < ins.length(); i++)
            if (idg.getID(ins.charAt(i)) == -1)
                return false;
        return true;
    }

    public String modulo(String ins){
        String res = "";
        for (int i = 0; i < ins.length(); i++) {
            int j = (i + 1) % size;
            res += alphabet.charAt((idg.getID(ins.charAt(i)) + j) % size);
        }
        return res;
    }
    public String antimodulo(String ins){
        String res = "";
        for (int i = 0; i < ins.length(); i++){
            int j = size - ((i + 1) % size);
            res += alphabet.charAt((idg.getID(ins.charAt(i)) + j) % size);
        }
        return res;
    }
}

interface IDgetter{
    int getID(char c);
}