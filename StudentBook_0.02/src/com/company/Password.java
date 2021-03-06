package com.company;

public final class Password {
    static final int shuffleSeedAlp = 17;
    static final int shuffleSeedPerm = 17;

    static final MyPasswordAlphabeticChanger mpac = new MyPasswordAlphabeticChanger(shuffleSeedAlp);

    private Password(){}

    public static boolean checkGood(String ins) {
        for (int i = 0; i < ins.length(); i++)
            if (MyPasswordAlphabeticChanger.getID(ins.charAt(i)) == -1)
                return false;
        return true;
    }

    public static String encrypt(String ins){
        String res = ins;
        res = mpac.modulo(res);
        res = mpac.changed(res);
        Shuffler shuffler = new Shuffler(res, shuffleSeedPerm);
        res = shuffler.shuffled();
        return res;
    }
    public static String decrypt(String ins){
        String res = ins;
        Shuffler shuffler = new Shuffler(res, shuffleSeedPerm);
        res = shuffler.unshuffled();
        res = mpac.antichanged(res);
        res = mpac.antimodulo(res);
        return res;
    }
}
