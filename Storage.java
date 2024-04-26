/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.calculator;

import java.awt.Frame;
import java.util.Stack;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

/**
 *
 * @author Dev
 */
public class Storage {

    private Stack<String> statements, results;
    final String TITLE = "History";

    public Storage() {
        this.statements = new Stack<>();
        this.results = new Stack<>();
        this.statements.push("");
        this.results.push("");
    }

    public void save(String statement, String result) {
        if (statement.equals("")) {
            return;
        }
        this.statements.push(statement);
        this.results.push(result);
    }

    public void showHistory(Frame frame) {
        int counter = statements.size() - 1;
        String history = "";
        while (counter >= 0) {
            history += String.format(statements.get(counter) + "%n" + results.get(counter) + "%n%n");
            counter--;
        }
        JOptionPane.showMessageDialog(frame, history, TITLE, PLAIN_MESSAGE);
    }
}
