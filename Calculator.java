/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.calculator;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextArea;
import static java.awt.TextArea.SCROLLBARS_HORIZONTAL_ONLY;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Dev
 * @date 1/4/2024
 */
public class Calculator {

    private static Frame window;
    private static TextArea tf, ph;
    private static Storage storage;
    private static String text;
    private static int counter, counterDeg;
    protected static boolean inDeg;

    private Calculator() {
        window = new Frame("Calculator");
        window.setBackground(Color.decode("#65C6DB"));
        window.setBounds(200, 150, 350, 500);
        window.setFont(Font.decode("ARIAL-PLAIN-16"));
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setLayout(null);
        window.setVisible(true);

        ph = new TextArea("", 1, 20, SCROLLBARS_HORIZONTAL_ONLY);
        ph.setEnabled(false);
        ph.setEditable(false);
        ph.setBounds(10, 35, 330, 40);
        tf = new TextArea("", 4, 20, SCROLLBARS_HORIZONTAL_ONLY);
        tf.setBounds(10, 80, 330, 115);

        text = "";
        counter = counterDeg = 0;
        inDeg = false;
        storage = new Storage();
    }

    public static void alert(String s) {
        JOptionPane.showMessageDialog(window, s);
    }

    public static String evaluate(String exp) {
        Stack<Double> operands = new Stack<>();  //Operand stack
        Stack<Character> operations = new Stack<>();  //Operator stack

        int length = exp.length();
        for (int i = 0; i < length; i++) {
            char c = exp.charAt(i);
            if (Character.isDigit(c)) { //check if it is number
                boolean isNegative = false;
                if (i > 1 && exp.charAt(i - 2) == '(' && exp.charAt(i - 1) == '-') {
                    operations.pop();
                    isNegative = true;
                }
                //Entry is Digit, and it could be greater than a one-digit number
                int num = 0;
                String number = "", decimalP = "";
                boolean afterDecimal = false;
                while (Character.isDigit(c)) {
                    if (!afterDecimal) {
                        num = num * 10 + (c - '0');
                        i++;
                        if (i < exp.length()) {
                            c = exp.charAt(i);
                            if (c == '.') {
                                afterDecimal = true;
                                number = num + ".";
                                i++;
                            }
                        } else {
                            break;
                        }
                    } else {
                        decimalP += c;
                        i++;
                    }
                    if (i < exp.length()) {
                        c = exp.charAt(i);
                    } else {
                        break;
                    }
                }
                i--;
                if (afterDecimal) {
                    if (isNegative) {
                        number = "-" + number + decimalP;
                    } else {
                        number += decimalP;
                    }
                } else {
                    if (isNegative) {
                        number = "-" + String.valueOf(num);
                    } else {
                        number = String.valueOf(num);
                    }
                }

                operands.push(Double.valueOf(number));
            } else if (c == '(') {
                operations.push(c);   //push character to operators stack
            } //Closed brace, evaluate the entire brace
            else if (c == ')') {
                while (operations.peek() != '(') {
                    if (operations.peek() == '%' && operands.size() == 2) {
                        operands.add(1, 1d);
                    }
                    double output = performOperation(operands, operations);
                    operands.push(output);   //push result back to stack
                }
                operations.pop();
            } // current character is operator
            else if (isOperator(c)) {
                if (!operations.isEmpty()) {
                    if (isOperator(exp.charAt(i - 1))) {
                        exp = exp.substring(0, i) + "(" + exp.substring(i) + ")";
                        length += 2;
                        i--;
                        continue;
                    }
                }
                while (!operations.isEmpty() && precedence(c) <= precedence(operations.peek())) {
                    double output = performOperation(operands, operations);
                    operands.push(output);   //push result back to stack
                }
                operations.push(c);   //push the current operator to stack
            }
        }

        while (!operations.isEmpty()) {
            if (operations.size() > 1 && operations.peek() == '%') {
                operands.add(0, operands.get(0));
            }
            double output = performOperation(operands, operations);
            operands.push(output);   //push final result back to stack
        }

        double result = 0;
        try {
            result = operands.pop();
        } catch (EmptyStackException ex) {
            ex.printStackTrace(System.err);
            alert("Invalid input");
            window.dispose();
            Calculator.main(new String[]{""});
        }

        String number = String.valueOf(result);
        String[] split = number.split("\\.");
        if (split[1].length() > 10) {
            number = String.format("%.10f", result);
        }
        String intNumber = split[0];

        if (Pattern.matches("[^123456789]", split[1])) {
            return intNumber;
        }
        return number;
    }

    static int precedence(char c) {
        switch (c) {
            case '!' -> {
                return 0;
            }
            case '+', '-' -> {
                return 1;
            }
            case 'x', '/', '%' -> {
                return 2;
            }
            case '^' -> {
                return 3;
            }
        }
        return -1;
    }

    public static double performOperation(Stack<Double> operands, Stack<Character> operations) {
        try {
            char operation = operations.pop();
            double a = operands.pop();

            switch (operation) {
                case '+' -> {
                    if (operands.isEmpty()) {
                        operands.push(0d);
                    }
                    double b = operands.pop();
                    return b + a;
                }
                case '-' -> {
                    if (operands.isEmpty()) {
                        operands.push(0d);
                    }
                    double b = operands.pop();
                    return b - a;
                }
                case 'x' -> {
                    if (operands.isEmpty()) {
                        operands.push(1d);
                    }
                    double b = operands.pop();
                    return b * a;
                }
                case '/' -> {
                    if (operands.isEmpty()) {
                        operands.push(1d);
                    }
                    double b = operands.pop();
                    if (a == 0) {
                        alert("Cannot divide by zero");
                        return 0.0;
                    }
                    return b / a;
                }
                case '%' -> {
                    if (operands.isEmpty()) {
                        return (a / 100.0);
                    }
                    double b = operands.pop();
                    return (b / 100.0) * a;
                }
                case '^' -> {
                    if (operands.isEmpty()) {
                        operands.push(1d);
                    }
                    double b = operands.pop();
                    return Math.pow(b, a);
                }
            }
            operations.add(0, operation);
            return a;
        } catch (EmptyStackException e) {
            alert("Wrong operation!");
            ph.setText("");
            return 0.0;
        }
    }

    public static boolean isOperator(char c) {
        return (c == '+' || c == '-' || c == '/' || c == 'x' || c == '^' || c == '%');
    }

    private static String evaluateLog(String exp) {
        if (!exp.contains("log")) {
            return exp;
        } else {
            int index = exp.lastIndexOf("log");
            int rpIndex = exp.indexOf(")", index);
            String sub = exp.substring(index + 4, rpIndex);
            if (sub == null) {
                return null;
            }
            double eval;
            if (sub.contains("sin") || sub.contains("cos") || sub.contains("tan")
                    || sub.contains("csc") || sub.contains("sec") || sub.contains("cot")) {
                eval = Double.parseDouble(new Trigonometery().evaluateTrig(sub));
            } else {
                eval = Double.parseDouble(evaluate(sub));
            }
            String evalT = String.format("%.12f", Math.log10(eval));
            if (evalT.equals("NaN")) {
                return null;
            }
            String newExp = exp.substring(0, index) + evalT + exp.substring(rpIndex + 1);
            return evaluateLog(newExp);
        }
    }

    public static long factorial(int n) {
        if (n <= 1) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    public static String reverse(String s) {
        String result = "";
        for (int i = s.length() - 1; i >= 0; i--) {
            result += s.charAt(i);
        }
        return result;
    }

    public static void main(String[] args) {

        Calculator calculator = new Calculator();

        Button history = new Button("history");
        Button delete = new Button("del");

        Button add = new Button("+");
        Button sub = new Button("-");
        Button mul = new Button("x");
        Button quo = new Button("/");
        Button per = new Button("%");
        Button res = new Button("=");
        Button euler = new Button("e");
        Button pi = new Button("\u03c0");
        Button fact = new Button("n!");

        Button uno = new Button("1");
        Button dos = new Button("2");
        Button tres = new Button("3");
        Button cuatro = new Button("4");
        Button cinco = new Button("5");
        Button seis = new Button("6");
        Button siete = new Button("7");
        Button ocho = new Button("8");
        Button nueve = new Button("9");
        Button cero = new Button("0");

        Button dot = new Button(".");
        Button clear = new Button("C");
        Button expo = new Button("^");
        Button log = new Button("log");

        Button sin = new Button("sin");
        Button cos = new Button("cos");
        Button tan = new Button("tan");
        Button csc = new Button("csc");
        Button sec = new Button("sec");
        Button cot = new Button("cot");
        Button inv = new Button("inv");
        Button deg = new Button("deg");
        Button sini = new Button("sin" + "-" + "\u00b9");
        Button cosi = new Button("cos" + "-" + "\u00b9");
        Button tani = new Button("tan" + "-" + "\u00b9");
        Button csci = new Button("csc" + "-" + "\u00b9");
        Button seci = new Button("sec" + "-" + "\u00b9");
        Button coti = new Button("cot" + "-" + "\u00b9");

        Button openP = new Button("(");
        Button closeP = new Button(")");

        // Set size of buttons
        history.setBounds(30, 205, 55, 30);
        delete.setBounds(270, 205, 55, 30);

        add.setBounds(30, 240, 55, 30);
        euler.setBounds(90, 240, 55, 30);
        pi.setBounds(150, 240, 55, 30);
        fact.setBounds(210, 240, 55, 30);
        clear.setBounds(270, 240, 55, 30);

        sub.setBounds(30, 275, 55, 30);
        uno.setBounds(90, 275, 55, 30);
        dos.setBounds(150, 275, 55, 30);
        tres.setBounds(210, 275, 55, 30);
        per.setBounds(270, 275, 55, 30);

        mul.setBounds(30, 310, 55, 30);
        cuatro.setBounds(90, 310, 55, 30);
        cinco.setBounds(150, 310, 55, 30);
        seis.setBounds(210, 310, 55, 30);
        expo.setBounds(270, 310, 55, 30);

        quo.setBounds(30, 345, 55, 30);
        siete.setBounds(90, 345, 55, 30);
        ocho.setBounds(150, 345, 55, 30);
        nueve.setBounds(210, 345, 55, 30);
        sin.setBounds(270, 345, 55, 30);

        inv.setBounds(30, 380, 55, 30);
        cero.setBounds(90, 380, 55, 30);
        dot.setBounds(150, 380, 55, 30);
        res.setBounds(210, 380, 55, 30);
        cos.setBounds(270, 380, 55, 30);

        deg.setBounds(30, 415, 55, 30);
        log.setBounds(90, 415, 55, 30);
        openP.setBounds(150, 415, 55, 30);
        closeP.setBounds(210, 415, 55, 30);
        tan.setBounds(270, 415, 55, 30);

        csc.setBounds(150, 450, 55, 30);
        sec.setBounds(210, 450, 55, 30);
        cot.setBounds(270, 450, 55, 30);

        // Set color for buttons
        history.setBackground(Color.decode("#8CD5E6"));
        delete.setBackground(Color.LIGHT_GRAY);

        add.setBackground(Color.decode("#8CD5E6"));
        euler.setBackground(Color.decode("#8CD5E6"));
        pi.setBackground(Color.decode("#8CD5E6"));
        fact.setBackground(Color.decode("#8CD5E6"));
        clear.setBackground(Color.decode("#8CD5E6"));

        sub.setBackground(Color.decode("#8CD5E6"));
        uno.setBackground(Color.decode("#8CD5E6"));
        dos.setBackground(Color.decode("#8CD5E6"));
        tres.setBackground(Color.decode("#8CD5E6"));
        per.setBackground(Color.decode("#8CD5E6"));

        mul.setBackground(Color.decode("#8CD5E6"));
        cuatro.setBackground(Color.decode("#8CD5E6"));
        cinco.setBackground(Color.decode("#8CD5E6"));
        seis.setBackground(Color.decode("#8CD5E6"));
        expo.setBackground(Color.decode("#8CD5E6"));

        quo.setBackground(Color.decode("#8CD5E6"));
        siete.setBackground(Color.decode("#8CD5E6"));
        ocho.setBackground(Color.decode("#8CD5E6"));
        nueve.setBackground(Color.decode("#8CD5E6"));
        sin.setBackground(Color.decode("#8CD5E6"));

        inv.setBackground(Color.decode("#8CD5E6"));
        cero.setBackground(Color.decode("#8CD5E6"));
        dot.setBackground(Color.decode("#8CD5E6"));
        res.setBackground(Color.decode("#8CD5E6"));
        cos.setBackground(Color.decode("#8CD5E6"));

        deg.setBackground(Color.decode("#8CD5E6"));
        log.setBackground(Color.decode("#8CD5E6"));
        openP.setBackground(Color.decode("#8CD5E6"));
        closeP.setBackground(Color.decode("#8CD5E6"));
        tan.setBackground(Color.decode("#8CD5E6"));

        csc.setBackground(Color.decode("#8CD5E6"));
        sec.setBackground(Color.decode("#8CD5E6"));
        cot.setBackground(Color.decode("#8CD5E6"));

        // Set font
        tf.setFont(Font.decode("ARIAL-PLAIN-18"));
        history.setFont(Font.decode("ARIAL-ITALIC-16"));
        fact.setFont(Font.decode("ARIAL-ITALIC-16"));

        window.add(ph);
        window.add(tf);
        window.add(clear);
        window.add(add);
        window.add(sub);
        window.add(mul);
        window.add(quo);
        window.add(per);
        window.add(expo);
        window.add(uno);
        window.add(dos);
        window.add(tres);
        window.add(cuatro);
        window.add(cinco);
        window.add(seis);
        window.add(siete);
        window.add(ocho);
        window.add(nueve);
        window.add(cero);
        window.add(dot);
        window.add(res);
        window.add(euler);
        window.add(pi);
        window.add(fact);
        window.add(sin);
        window.add(cos);
        window.add(tan);
        window.add(inv);
        window.add(openP);
        window.add(closeP);
        window.add(deg);
        window.add(log);
        window.add(history);
        window.add(delete);
        window.add(csc);
        window.add(sec);
        window.add(cot);

        delete.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                text = text.substring(0, text.length() - 1);
                tf.setText(text);
            }
        });

        inv.addActionListener((ActionEvent e) -> {
            if (counter % 2 == 0) {
                sini.setBounds(270, 345, 55, 30);
                cosi.setBounds(270, 380, 55, 30);
                tani.setBounds(270, 415, 55, 30);
                csci.setBounds(150, 450, 55, 30);
                seci.setBounds(210, 450, 55, 30);
                coti.setBounds(270, 450, 55, 30);
                sini.setBackground(Color.decode("#8CD5E6"));
                cosi.setBackground(Color.decode("#8CD5E6"));
                tani.setBackground(Color.decode("#8CD5E6"));
                csci.setBackground(Color.decode("#8CD5E6"));
                seci.setBackground(Color.decode("#8CD5E6"));
                coti.setBackground(Color.decode("#8CD5E6"));

                window.remove(sin);
                window.remove(cos);
                window.remove(tan);
                window.remove(csc);
                window.remove(sec);
                window.remove(cot);

                window.add(sini);
                window.add(cosi);
                window.add(tani);
                window.add(csci);
                window.add(seci);
                window.add(coti);
            } else {
                window.remove(sini);
                window.remove(cosi);
                window.remove(tani);
                window.remove(csci);
                window.remove(seci);
                window.remove(coti);

                window.add(sin);
                window.add(cos);
                window.add(tan);
                window.add(csc);
                window.add(sec);
                window.add(cot);

            }
            counter++;
        });

        deg.addActionListener((ActionEvent e) -> {
            if (counterDeg % 2 == 0) {
                inDeg = true;
                deg.setBackground(Color.BLUE);
            } else {
                inDeg = false;
                deg.setBackground(Color.decode("#8CD5E6"));
            }
            counterDeg++;
        });

        tf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key = e.getKeyChar();
                switch (key) {
                    case '*' -> {
                        text += "x";
                    }
                    case 'e' -> {
                        text += String.format("%.12f", Math.E);
                    }
                    case KeyEvent.CHAR_UNDEFINED ->
                        text += "";
                    default ->
                        text += key;
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!text.equals("")) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        ph.setText(text);
                        if (text.contains("log") || text.contains("sin")
                                || text.contains("cos") || text.contains("tan")
                                || text.contains("csc") || text.contains("sec")
                                || text.contains("cot")) {
                            try {
                                text = evaluateLog(text);
                                text = evaluate(new Trigonometery().evaluateTrig(text));

                            } catch (StringIndexOutOfBoundsException ex) {
                                alert("Wrong operation!");
                            } catch (NullPointerException ex) {
                                alert("Undefined!");
                            }
                        } else {
                            text = evaluate(text);
                        }
                        tf.setText(text);
                        storage.save(ph.getText(), text);
                        tf.setCaretPosition(text.length());
                    }
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        text = tf.getText();
                    }
                }
            }
        });

        history.addActionListener((ActionEvent e) -> {
            storage.showHistory(window);
        });

        uno.addActionListener((ActionEvent e) -> {
            text += "1";
            tf.setText(text);
        });

        dos.addActionListener((ActionEvent e) -> {
            text += "2";
            tf.setText(text);
        });

        tres.addActionListener((ActionEvent e) -> {
            text += "3";
            tf.setText(text);
        });

        cuatro.addActionListener((ActionEvent e) -> {
            text += "4";
            tf.setText(text);
        });

        cinco.addActionListener((ActionEvent e) -> {
            text += "5";
            tf.setText(text);
        });

        seis.addActionListener((ActionEvent e) -> {
            text += "6";
            tf.setText(text);
        });

        siete.addActionListener((ActionEvent e) -> {
            text += "7";
            tf.setText(text);
        });

        ocho.addActionListener((ActionEvent e) -> {
            text += "8";
            tf.setText(text);
        });

        nueve.addActionListener((ActionEvent e) -> {
            text += "9";
            tf.setText(text);
        });

        cero.addActionListener((ActionEvent e) -> {
            text += "0";
            tf.setText(text);
        });

        dot.addActionListener((ActionEvent e) -> {
            if (text.equals("") || isOperator(text.charAt(text.length() - 1))) {
                text += "0.";
                tf.setText(text);
            } else {
                text += ".";
                tf.setText(text);
            }
        });

        add.addActionListener((ActionEvent e) -> {
            text += "+";
            tf.setText(text);
        });

        sub.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                char lastC = text.charAt(text.length() - 1);
                if (isOperator(lastC)) {
                    text += "(-";
                    tf.setText(text);
                } else {
                    text += "-";
                    tf.setText(text);
                }
            } else {
                text += "-";
                tf.setText(text);
            }
        });

        mul.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                text += "x";
                tf.setText(text);
            } else {
                alert("Wrong operation!");
                ph.setText("");
                tf.setText("");
            }
        });

        quo.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                text += "/";
                tf.setText(text);
            } else {
                alert("Wrong operation!");
                ph.setText("");
                tf.setText("");
            }
        });

        per.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                text += "%";
                tf.setText(text);
            } else {
                alert("Wrong operation!");
                ph.setText("");
                tf.setText("");
            }
        });

        expo.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                text += "^";
                tf.setText(text);
            } else {
                alert("Wrong operation!");
                ph.setText("");
                tf.setText("");
            }
        });

        euler.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                    tf.setText(text);
                }
                tf.setText(text + String.format("%.12f", Math.E));
            } else {
                tf.setText(String.format("%.12f", Math.E));
            }
            text = tf.getText();
        });

        pi.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    tf.setText(text + "x");
                    text += "x";
                }
                tf.setText(text + String.format("%.12f", Math.PI));
            } else {
                tf.setText(String.format("%.12f", Math.PI));
            }
            text = tf.getText();
        });

        fact.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                String num = "";
                boolean isClosedP = false;
                int i;
                for (i = text.length() - 1; i >= 0; i--) {
                    char c = text.charAt(i);
                    if (Character.isDigit(c)) {
                        num += text.charAt(i);
                    } else if (c == '.') {
                        alert("Factorial of only integers supported!");
                        num = "";
                        break;
                    } else if (c == ')') {
                        isClosedP = true;
                        break;
                    } else {
                        break;
                    }
                }

                long factorial = factorial(Integer.parseInt(reverse(num)));
                if (isClosedP) {
                    text = text.substring(0, i + 1) + "x" + factorial;
                    tf.setText(text);
                } else {
                    text = text.substring(0, i + 1) + factorial;
                    tf.setText(text);
                }
            }
        });

        openP.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "(";
            tf.setText(text);
        });

        closeP.addActionListener((ActionEvent e) -> {
            text += ")";
            tf.setText(text);
        });

        sin.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "sin(";
            tf.setText(tf.getText() + "sin(");
        });

        cos.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "cos(";
            tf.setText(text);
        });

        tan.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "tan(";
            tf.setText(text);
        });

        csc.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "csc(";
            tf.setText(text);
        });

        sec.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "sec(";
            tf.setText(text);
        });

        cot.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "cot(";
            tf.setText(text);
        });

        sini.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "sin-" + "\u00b9" + "(";
            tf.setText(text);
        });

        cosi.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "cos-" + "\u00b9" + "(";
            tf.setText(text);
        });

        tani.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "tan-" + "\u00b9" + "(";
            tf.setText(text);
        });

        csci.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "csc-" + "\u00b9" + "(";
            tf.setText(text);
        });

        seci.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "sec-" + "\u00b9" + "(";
            tf.setText(text);
        });

        coti.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                if (Character.isDigit(text.charAt(text.length() - 1))) {
                    text += "x";
                }
            }
            text += "cot-" + "\u00b9" + "(";
            tf.setText(text);
        });

        log.addActionListener((ActionEvent e) -> {
            if (!text.equals("")) {
                char last = text.charAt(text.length() - 1);
                if (last == ')' || Character.isDigit(last)) {
                    text += "xlog(";
                } else {
                    text += "log(";
                }
            } else {
                text += "log(";
            }
            tf.setText(text);
        });

        clear.addActionListener((ActionEvent e) -> {
            text = "";
            ph.setText("");
            tf.setText("");
        });

        res.addActionListener((ActionEvent e) -> {
            ph.setText(text);
            try {
                if (text.contains("log")) {
                    text = evaluateLog(text);
                }
                if (text.contains("sin") || text.contains("cos") || text.contains("tan")
                        || text.contains("csc") || text.contains("sec") || text.contains("cot")) {
                    text = new Trigonometery().evaluateTrig(text);
                }
                text = evaluate(text);
                tf.setText(text);
                storage.save(ph.getText(), text);
            } catch (StringIndexOutOfBoundsException ex) {
                ex.printStackTrace(System.err);
                alert("Wrong operation!");
            } catch (NullPointerException ex) {
                alert("Undefined!");
            }
        });

        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
