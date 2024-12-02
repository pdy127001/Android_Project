package com.example.project4;

import java.util.Random;

public class MathProblemGenerator {
    private Random random;

    public MathProblemGenerator() {
        random = new Random();
    }

    public String[] generateProblem() {
        int num1 = random.nextInt(10); // 0-9
        int num2 = random.nextInt(10); // 0-9
        int operation = random.nextInt(2); // 0: 덧셈, 1: 뺄셈

        String question;
        int answer;

        if (operation == 0) {
            question = num1 + " + " + num2 + " = ?";
            answer = num1 + num2;
        } else {
            question = num1 + " - " + num2 + " = ?";
            answer = num1 - num2;
        }

        return new String[]{question, String.valueOf(answer)};
    }
}
