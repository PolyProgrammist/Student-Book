package com.company;

import javax.swing.*;
import java.awt.*;

public class TestPanel extends JPanel {
    JLabel testTaskTextLabel;
    JLabel testThisProblemLabel;
    JTextField testAnswerField;
    JButton testAnswerButton;
    SimpleTest st;

    private void checkAnswer(int tid){
        String usans = testAnswerField.getText();
        if (usans == null || usans.equals(""))
            return;
        if (usans.equals(st.answer)) {
            st = SimpleTest.getTest(tid);
            testThisProblemLabel.setText(st.expression);
            testAnswerField.setText("");
            testAnswerButton.setText("Right");
        }
        else
            testAnswerButton.setText("Wrong");
    }
    public TestPanel(int tid){
        if (tid == -1)
            return;
        JPanel s = new JPanel();
        st = SimpleTest.getTest(tid);
        if (st != null){
            testTaskTextLabel = new JLabel(st.task);
            testThisProblemLabel = new JLabel(st.expression);
            testAnswerField = new JTextField("");
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
