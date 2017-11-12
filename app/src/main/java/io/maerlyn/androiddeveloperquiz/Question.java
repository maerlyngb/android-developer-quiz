package io.maerlyn.androiddeveloperquiz;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maerlyn Broadbent
 */

public class Question {

    private String text;
    private QuestionType type;
    private List<Answer> answers;
    private Boolean isCorrect;

    Question() {
        answers = new ArrayList<>();
        type = QuestionType.SINGLE;
        isCorrect = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    QuestionType getType() {
        return type;
    }

    void setType(QuestionType type) {
        this.type = type;
    }

    List<Answer> getAnswers() {
        return answers;
    }

    void newAnswer(String answerText, Boolean isCorrect) {
        Answer newAnswer = new Answer();
        newAnswer.setText(answerText);
        newAnswer.setCorrect(isCorrect);

        this.answers.add(newAnswer);
    }

    /**
     * Check the answers to this question and determine if the
     * user was correct
     */
    Boolean isCorrect(){
        switch (type) {
            case SINGLE:
                checkSingle();
                break;

            case MULTIPLE:
                checkMultiple();
                break;

            case FREETEXT:
                checkFreeText();
                break;
        }

        return isCorrect;
    }

    /**
     * check a radio button question to see if the user selected
     * the correct answer
     */
    private void checkSingle() {

        for (Answer answer : answers) {
            if (answer.getSelected() && answer.getCorrect()) {
                isCorrect = true;
            }
        }
    }

    /**
     * check a checkbox question to see if the user selected
     * the correct answers
     */
    private void checkMultiple() {
        for (Answer answer : answers) {
            if (!(answer.getCorrect() == answer.getSelected())) {
                isCorrect = false;
                return;
            }
        }

        isCorrect = true;
    }

    /**
     * Check to see if the text that the user has input
     *  matches the correct answer
     */
    private void checkFreeText() {

        // we should only have one for free text questions
        Answer answer = answers.get(0);

        String userInput = answer.getUserFreeText();
        String correctResponse = answer.getText();

        // if either is null, the user can't be right
        // the program will also throw an exception when
        // performing the comparison if one string is null
        if (userInput != null && correctResponse != null) {

            // only exact responses please!
            isCorrect = userInput.equals(correctResponse);
        }
    }
}
