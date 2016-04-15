package com.company;

import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.util.Random;

/**
 * Created by vadim on 15.04.2016.
 */
public class SimpleTest {
    String task, expression;
    String answer;

    public SimpleTest() {
        task = new String();
        expression = new String();
        answer = new String();
    }

    /*public static SimpleTest addition(){
        SimpleTest ad = new SimpleTest();
        ad.task = "Count the sum of 2 numbers";
        Random rnd = new Random(System.currentTimeMillis());
        Integer a = Math.abs(rnd.nextInt()) % 20, b = Math.abs(rnd.nextInt()) % 20;
        ad.expression = a.toString() + " + " + b.toString();
        ad.answer = (new Integer(a + b)).toString();
        return ad;
    }*/
    public static SimpleTest addition2(String fileName){
        SimpleTest ad = new SimpleTest();
        System.out.println("ad2 reached");
        PythonInterpreter pi = new PythonInterpreter();
        System.out.println("interpretator created");
        pi.execfile(fileName);
        System.out.println("executed");
        ad.task = ((PyString)pi.get("task")).asString();
        ad.expression = ((PyString)pi.get("expression")).asString();
        ad.answer = ((PyString)pi.get("answer")).asString();
        System.out.println("ad 2 fin");
        return ad;
    }

    public static SimpleTest getTest(int lid) {
        File file = new File(Main.lessonFileName[lid] + ".py");
        if (file.exists())
            return addition2(Main.lessonFileName[lid] + ".py");
        return null;
    }
}
