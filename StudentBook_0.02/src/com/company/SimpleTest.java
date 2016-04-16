package com.company;

import org.python.core.*;
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

    public static SimpleTest addition2(String fileName){
        SimpleTest ad = new SimpleTest();
        PySystemState.initialize(  );
        PythonInterpreter pi = new PythonInterpreter();
        pi.execfile(fileName);
        ad.task = (pi.get("task")).asString();
        ad.expression = (pi.get("expression")).asString();
        ad.answer = (pi.get("answer")).asString();
        return ad;
    }

    public static SimpleTest getTest(int lid) {
        File file = new File("src\\PythonTests\\" + Main.lessonFileName[lid] + ".py");
        if (file.exists())
            return addition2("src\\PythonTests\\" + Main.lessonFileName[lid] + ".py");
        return null;
    }
}
