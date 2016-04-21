package com.company;

import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.File;

public class SimpleTest {
    String task, expression;
    String answer;

    public SimpleTest() {
        task = new String();
        expression = new String();
        answer = new String();
    }

    public static SimpleTest addition(String fileName){
        SimpleTest ad = new SimpleTest();
        PySystemState.initialize();
        PythonInterpreter pi = new PythonInterpreter();
        pi.execfile(fileName);
        ad.task = (pi.get("task")).asString();
        ad.expression = (pi.get("expression")).asString();
        ad.answer = (pi.get("answer")).asString();
        return ad;
    }

    public static SimpleTest getTest(int tid) {
        String s = PathConstants.FL + PathConstants.PYTHONLIBS_WAY + Main.testFileName[tid] + ".py";
        File file = new File(s);
        if (file.exists())
            return addition(s);
        return null;
    }
}
