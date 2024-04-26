/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.calculator;

import static com.mycompany.calculator.Calculator.evaluate;

/**
 *
 * @author Dev
 */
public class Trigonometery {

    public Trigonometery() {

    }

    private boolean isMax(int[] indices, int index) {
        boolean max = true;
        for (int i = indices.length - 2; i >= 0; i--) {
            if (index < indices[i]) {
                max = false;
                break;
            }
        }
        return max;
    }

    private String operateOnTrig(double eval, int op) {
        boolean inDeg = Calculator.inDeg;
        if (inDeg && op >= 6) {
            if (eval == 90.0 && op == 8) {
                return null;
            } else if (eval == 90.0 && op == 10) {
                return null;
            } else if (eval == 90.0 && op == 9) {
                return null;
            }
            eval = (eval * Math.PI) / 180.0;
        }

        eval = switch (op) {
            case 0 ->
                Math.asin(eval);
            case 1 ->
                Math.acos(eval);
            case 2 ->
                Math.atan(eval);
            case 3 ->
                Math.asin(1.0 / eval);
            case 4 ->
                Math.acos(1.0 / eval);
            case 5 ->
                Math.atan(1.0 / eval);
            case 6 ->
                Math.sin(eval);
            case 7 ->
                Math.cos(eval);
            case 8 ->
                Math.tan(eval);
            case 9 ->
                1.0 / Math.sin(eval);
            case 10 ->
                1.0 / Math.cos(eval);
            case 11 ->
                1.0 / Math.tan(eval);
            default ->
                eval;
        };

        if (inDeg && op < 6) {
            eval = (eval * 180.0) / Math.PI;
        }

        String evalT = String.format("%.12f", eval);
        if (evalT.equals("NaN")) {
            return null;
        }
        return evalT;
    }

    private String evaluateInverse(String exp, int anum) {
        int index = switch (anum) {
            case 0 ->
                exp.lastIndexOf("sin-" + "\u00b9");
            case 1 ->
                exp.lastIndexOf("cos-" + "\u00b9");
            case 2 ->
                exp.lastIndexOf("tan-" + "\u00b9");
            case 3 ->
                exp.lastIndexOf("csc-" + "\u00b9");
            case 4 ->
                exp.lastIndexOf("sec-" + "\u00b9");
            case 5 ->
                exp.lastIndexOf("cot-" + "\u00b9");
            default ->
                -1;
        };

        int rpIndex = exp.indexOf(")", index);
        String sub = exp.substring(index + 6, rpIndex);
        if (sub == null) {
            return null;
        }
        double eval;
        if (sub.contains("sin") || sub.contains("cos") || sub.contains("tan")
                || sub.contains("csc") || sub.contains("sec")
                || sub.contains("cot")) {
            eval = Double.parseDouble(evaluateTrig(sub));
        } else {
            eval = Double.parseDouble(evaluate(sub));
        }
        String evalT = operateOnTrig(eval, anum);
        String newExp = exp.substring(0, index) + evalT + exp.substring(rpIndex + 1);
        return evaluateTrig(newExp);
    }

    private String evaluateFunc(String exp, int anum) {
        int index = switch (anum) {
            case 6 ->
                exp.lastIndexOf("sin");
            case 7 ->
                exp.lastIndexOf("cos");
            case 8 ->
                exp.lastIndexOf("tan");
            case 9 ->
                exp.lastIndexOf("csc");
            case 10 ->
                exp.lastIndexOf("sec");
            case 11 ->
                exp.lastIndexOf("cot");
            default ->
                -1;
        };

        int rpIndex = exp.indexOf(")", index);
        String sub = exp.substring(index + 4, rpIndex);
        if (sub == null) {
            return null;
        }
        double eval;
        if (sub.contains("sin") || sub.contains("cos") || sub.contains("tan")
                || sub.contains("csc") || sub.contains("sec")
                || sub.contains("cot")) {
            eval = Double.parseDouble(evaluateTrig(sub));
        } else {
            eval = Double.parseDouble(evaluate(sub));
        }
        String evalT = operateOnTrig(eval, anum);
        String newExp = exp.substring(0, index) + evalT + exp.substring(rpIndex + 1);
        return evaluateTrig(newExp);
    }

    protected String evaluateTrig(String exp) {
        if (exp.contains("sin") || exp.contains("cos") || exp.contains("tan")
                || exp.contains("csc") || exp.contains("sec")
                || exp.contains("cot")) {
            int[] indices = new int[7];
            indices[0] = exp.lastIndexOf("sin");
            indices[1] = exp.lastIndexOf("cos");
            indices[2] = exp.lastIndexOf("tan");
            indices[3] = exp.lastIndexOf("csc");
            indices[4] = exp.lastIndexOf("sec");
            indices[5] = exp.lastIndexOf("cot");
            indices[6] = exp.lastIndexOf("\u00b9");

            int anum = -1, invi = indices[6];
            if (invi != -1) {
                if (invi > indices[2] && isMax(indices, indices[2])) {
                    anum = 2;
                } else if (invi > indices[1] && isMax(indices, indices[1])) {
                    anum = 1;
                } else if (invi > indices[0] && isMax(indices, indices[0])) {
                    anum = 0;
                } else if (invi > indices[3] && isMax(indices, indices[3])) {
                    anum = 3;
                } else if (invi > indices[4] && isMax(indices, indices[4])) {
                    anum = 4;
                } else if (invi > indices[5] && isMax(indices, indices[5])) {
                    anum = 5;
                }
            } else {
                if (isMax(indices, indices[0])) {
                    anum = 6;
                } else if (isMax(indices, indices[1])) {
                    anum = 7;
                } else if (isMax(indices, indices[2])) {
                    anum = 8;
                } else if (isMax(indices, indices[3])) {
                    anum = 9;
                } else if (isMax(indices, indices[4])) {
                    anum = 10;
                } else if (isMax(indices, indices[5])) {
                    anum = 11;
                }
            }

            switch (anum) {
                case 0, 1, 2, 3, 4, 5 -> {
                    return evaluateInverse(exp, anum);
                }
                case 6, 7, 8, 9, 10, 11 -> {
                    return evaluateFunc(exp, anum);
                }
                default -> {
                    return exp;
                }
            }
        } else {
            return exp;
        }
    }
}
