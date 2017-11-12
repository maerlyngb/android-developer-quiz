package io.maerlyn.androiddeveloperquiz;

/**
 * @author Maerlyn Broadbent
 */

public class Answer {
    private int id;
    private String text;
    private Boolean isCorrect;
    private Boolean isSelected;
    private String userFreeText;

    Answer(){
        isSelected = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    Boolean getCorrect() {
        return isCorrect;
    }

    void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    Boolean getSelected() {
        return isSelected;
    }

    void setSelected(Boolean selected) {
        isSelected = selected;
    }

    String getUserFreeText() {
        return userFreeText;
    }

    void setUserFreeText(String userFreeText) {
        this.userFreeText = userFreeText;
    }
}
