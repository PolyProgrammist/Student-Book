package com.company;

import javax.swing.*;
import java.awt.*;

public class TestPanel extends JPanel {
    JLabel testTaskTextLabel;
    JLabel testThisProblemLabel;
    JTextField testAnswerField;
    JButton testAnswerButton;
    SimpleTest st;

    private boolean goodAnswer(String usans) {
        return usans == st.answer;
    }
    private void checkAnswer(int tid){
        String usans = testAnswerField.getText();
        if (usans == null || usans.equals(""))
            return;
        if (goodAnswer(usans)) {
            st = SimpleTest.getTest(tid);
            testThisProblemLabel.setText(st.expression);
            testAnswerField.setText("");
            testAnswerButton.setText("Right");
        }
        else
            testAnswerButton.setText("Wrong");
    }
    public TestPanel(int tid){
        JPanel s = new JPanel();
        st = SimpleTest.getTest(tid);
        if (st != null){
            testTaskTextLabel = new JLabel(st.task);
            testThisProblemLabel = new JLabel(st.expression);
            testAnswerField = new JTextField("");
            testAnswerButton = new JButton("Check answer");
            testAnswerButton.addActionListener(e -> checkAnswer(tid));
            testAnswerField.addActionListener(e -> checkAnswer(tid));

            setLayout(new BorderLayout());
            add(testTaskTextLabel, BorderLayout.NORTH);
            add(testThisProblemLabel, BorderLayout.CENTER);
            add(testAnswerField, BorderLayout.SOUTH);
            add(testAnswerButton, BorderLayout.EAST);

            setVisible(true);
        }
        else
            setVisible(false);
    }
}
