package com.example.myapplication;

public class QuizQuestion {
    private String question;
    private String[] optionslist;
    private int correctAnswer;

    public QuizQuestion(String question, String[] options, int correctAnswerIndex) {
        this.question = question;
        this.optionslist = options;
        this.correctAnswer = correctAnswerIndex;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getOptionslist() {
        return optionslist;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswer;
    }
}

