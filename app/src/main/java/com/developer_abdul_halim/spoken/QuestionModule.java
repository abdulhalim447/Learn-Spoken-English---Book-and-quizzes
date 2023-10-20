package com.developer_abdul_halim.spoken;

import java.util.ArrayList;
import java.util.List;

public class QuestionModule {

    private String question;
    private ArrayList<String> answers = new ArrayList<>();
    private String rightAnswer;
    private boolean answeredCorrectly;  // Added this field to keep track of the answer status

    public QuestionModule(String question, String rightAnswer, String ... answers ) {
        this.question = question;
        this.rightAnswer = rightAnswer;

        if(answers.length == 4){
            this.answers.add(answers[0]);
            this.answers.add(answers[1]);
            this.answers.add(answers[2]);
            this.answers.add(answers[3]);
        } else {
            throw new IllegalArgumentException("Expected 4 answers for a question.");
        }
    }

    // Getter methods
    public String getQuestion() {
        return question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    // Getter and setter for the answeredCorrectly field
    public boolean isAnsweredCorrectly() {
        return answeredCorrectly;
    }

    public void setAnsweredCorrectly(boolean answeredCorrectly) {
        this.answeredCorrectly = answeredCorrectly;
    }
}
