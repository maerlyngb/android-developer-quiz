package io.maerlyn.androiddeveloperquiz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maerlyn Broadbent
 */
public class MainActivity extends AppCompatActivity {

    // Layout to contain all the questions
    public LinearLayout answerLayout;

    public List<Question> questions;
    public int activeQuestion;

    // needed so we only ever display a single toast
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // All questions will be displayed within this layout
        answerLayout = findViewById(R.id.answer_layout);

        startNewQuiz(null);
    }

    /**
     * setup the application for a new quiz
     *
     * @param view this activity
     */
    public void startNewQuiz(View view) {
        loadData();
        activeQuestion = 0;
        displayInitialView(activeQuestion);
    }

    /**
     * Display a question and it's possible answers to the screen
     *
     * @param questionIndex the index of the question to display
     */
    private void displayInitialView(int questionIndex) {
        Question question = questions.get(questionIndex);

        displayQuestion(question.getText());
        displayPossibleAnswers(question.getAnswers(), question.getType());
        updateProgressText();
        setInitialViewVisibility();
    }

    /**
     * set view visibility for a new quiz
     */
    private void setInitialViewVisibility() {
        setVisibility(R.id.progress_text, View.VISIBLE);
        setVisibility(R.id.previous_question_button, View.INVISIBLE);
        setVisibility(R.id.next_question_button, View.VISIBLE);
        setVisibility(R.id.finish_button, View.GONE);
        setVisibility(R.id.restart_button, View.GONE);
        setVisibility(R.id.summary, View.GONE);
    }

    /**
     * Display a question to the screen
     *
     * @param text of the question to display
     */
    private void displayQuestion(String text) {
        TextView questionView = findViewById(R.id.question_text);
        questionView.setText(text);
    }

    /**
     * Display the list of possible answers.
     * <p>
     * The output will differ depending on the given question type
     *
     * @param answers      list of answers to display
     * @param questionType determines how to display the options
     */
    private void displayPossibleAnswers(List<Answer> answers, QuestionType questionType) {
        switch (questionType) {
            case SINGLE:
                displaySingleTypeAnswers(answers);
                break;

            case MULTIPLE:
                displayMultipleTypeAnswers(answers);
                break;

            case FREETEXT:
                displayFreeTextTypeAnswers(answers);
                break;
        }
    }

    /**
     * Updates the progress text at the top of the screen
     */
    private void updateProgressText() {
        TextView progressText = findViewById(R.id.progress_text);

        progressText.setText(getString(R.string.progress_text,
                activeQuestion + 1, questions.size()));
    }


    /**
     * Display radio buttons for a given list of answers
     *
     * @param answers list of answers to display
     */
    private void displaySingleTypeAnswers(List<Answer> answers) {
        // needed to make enforce the user to select only one option
        RadioGroup radioGroup = new RadioGroup(this);

        // we only want the options for this question
        answerLayout.removeAllViews();

        for (Answer answer : answers) {
            RadioButton radioButton = new RadioButton(this);

            int padding = dpToPx(8);
            radioButton.setPadding(padding, padding, padding, padding);

            // generate an id so we can connect the answer objects
            // to the radio button
            int id = View.generateViewId();
            answer.setId(id);
            radioButton.setId(id);

            radioButton.setTextAppearance(this, R.style.Answer);
            radioButton.setText(answer.getText());

            // when a user selects an answer, we need to record it
            radioButton.setOnClickListener(this::radioButtonChecked);

            // radio buttons are cleared when the user switches between questions
            // this loads the previous choice from memory so we accurately display
            // what we're recording
            radioButton.setChecked(answer.getSelected());

            // add this radio button to the group
            radioGroup.addView(radioButton);
        }

        // display the radio button group
        answerLayout.addView(radioGroup);
    }

    /**
     * Display check boxes for a given list of answers
     *
     * @param answers list of answers to display
     */
    private void displayMultipleTypeAnswers(List<Answer> answers) {
        // we only want the options for this question
        answerLayout.removeAllViews();

        for (Answer answer : answers) {
            CheckBox checkBox = new CheckBox(this);

            int padding = dpToPx(8);
            checkBox.setPadding(padding, padding, padding, padding);

            // generate an id so we can connect the answer objects
            // to the radio button
            int id = View.generateViewId();
            answer.setId(id);
            checkBox.setId(id);

            checkBox.setTextAppearance(this, R.style.Answer);
            checkBox.setText(answer.getText());

            // when a user selects an answer, we need to record it
            checkBox.setOnClickListener(this::checkBoxChecked);

            // check boxes are cleared when the user switches between questions
            // this loads the previous choice from memory so we accurately display
            // what we're recording
            checkBox.setChecked(answer.getSelected());

            // display the checkbox
            answerLayout.addView(checkBox);
        }
    }

    /**
     * display an input field for a free text answer
     */
    private void displayFreeTextTypeAnswers(List<Answer> answers) {
        // we only want the options for this question
        answerLayout.removeAllViews();

        // we should only have a single answer for free text fields
        Answer answer = answers.get(0);

        EditText editText = new EditText(this);

        // generate an id so we can connect the answer objects
        // to the radio button
        int id = View.generateViewId();
        answer.setId(id);
        editText.setId(id);

        editText.setTextAppearance(this, R.style.Answer);
        editText.setHint(getText(R.string.free_text_answer_hint));

        // the input view is cleared when the user switches between questions
        // this loads the previous choice from memory so we accurately display
        // what we're recording
        editText.setText(answer.getUserFreeText());

        // we need to record when the text has changed so we can update
        // our answer object
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                updateFreeText(s.toString());
            }
        });

        // display edit text view
        answerLayout.addView(editText);
    }

    /**
     * Records which radio button a user has selected
     *
     * @param view the radio button that was clicked on
     */
    private void radioButtonChecked(View view) {
        List<Answer> answers = questions.get(activeQuestion).getAnswers();

        // we need to loop through all the answers so we can clear any previous
        // answer that was selected
        for (Answer answer : answers) {
            // only one option can be true with a radio button group
            answer.setSelected(answer.getId() == view.getId());
        }
    }

    /**
     * Records which checkboxes a user has selected
     *
     * @param view checkbox that was clicked on
     */
    private void checkBoxChecked(View view) {
        List<Answer> answers = questions.get(activeQuestion).getAnswers();

        // we need to loop through all the answers so we can clear any previous
        // answer that was selected
        for (Answer answer : answers) {

            // checkboxes work independently so we only need to change
            // the one that has just been clicked
            if (answer.getId() == view.getId()) {
                answer.setSelected(((CheckBox) view).isChecked());
            }
        }
    }

    /**
     * Records which radio button a user has selected
     *
     * @param userInput text that the user has typed as the answer
     */
    private void updateFreeText(String userInput) {
        List<Answer> answers = questions.get(activeQuestion).getAnswers();

        // we should only have one answer but using a loop ensures that
        // we won't lose our user's input data
        for (Answer answer : answers) {
            answer.setUserFreeText(userInput);
        }
    }

    /**
     * Check all the answers for correctness
     */
    public int checkAnswers() {
        int correctCount = 0;

        for (Question question : questions) {
            // count up how many questions  the user has gotten correct
            if (question.isCorrect()) {
                correctCount++;
            }
        }

        // display a toast showing the result
        showToast(correctCount + " out of " + questions.size());

        return correctCount;
    }

    /**
     * Display the next question to the screen
     * <p>
     * If there are no more questions in the list, show a toast
     * to the user to inform them
     *
     * @param view current activity
     */
    public void nextQuestion(View view) {
        if (activeQuestion < questions.size() - 1) {
            answerLayout.removeAllViews();
            activeQuestion++;
            displayInitialView(activeQuestion);

            // show the previous button so we can go back
            setVisibility(R.id.previous_question_button, View.VISIBLE);

        }

        if (activeQuestion == questions.size() - 1) {
            // we're at the end of the line
            setVisibility(R.id.next_question_button, View.GONE);

            // show the finish button so the user can get their final score
            setVisibility(R.id.finish_button, View.VISIBLE);
        }
    }

    /**
     * Display the previous question to the screen.
     * <p>
     * If there are no more questions in the list, show a toast
     * to the user to inform them.
     *
     * @param view current activity
     */
    public void prevQuestion(View view) {
        if (activeQuestion > 0) {
            answerLayout.removeAllViews();
            activeQuestion--;
            displayInitialView(activeQuestion);

            // we can go back further
            setVisibility(R.id.previous_question_button, View.VISIBLE);

            // if we've gone back, we can go forwards
            setVisibility(R.id.next_question_button, View.VISIBLE);

            // we can't finish early
            setVisibility(R.id.finish_button, View.GONE);
        }

        if (activeQuestion == 0) {
            // we're on the first question and can't go back any further!
            setVisibility(R.id.previous_question_button, View.INVISIBLE);
        }
    }

    /**
     * Set the visibility for a given view
     *
     * @param viewId     the id of the view to adjust
     * @param visibility the visibility to set the view to
     */
    private void setVisibility(int viewId, int visibility) {
        View view = findViewById(viewId);
        view.setVisibility(visibility);
    }

    /**
     * Finish the quiz and present the user with their score
     *
     * @param view the view that triggered this method
     */
    public void finishQuiz(View view) {
        // one ahead of index
        activeQuestion++;

        // we've already finished
        setVisibility(R.id.finish_button, View.GONE);

        // can't go back now
        setVisibility(R.id.previous_question_button, View.GONE);

        // some people may want to play again
        setVisibility(R.id.restart_button, View.VISIBLE);

        setVisibility(R.id.progress_text, View.GONE);

        answerLayout.removeAllViews();

        displaySummary();
    }

    /**
     * display the final score along with which questions were correct and which were incorrect
     */
    private void displaySummary() {
        int correctCount = checkAnswers();

        String finishMsg;

        if (correctCount < 3) {
            finishMsg = getString(R.string.finish_msg_1);
        } else if (correctCount < 6) {
            finishMsg = getString(R.string.finish_msg_2);
        } else if (correctCount < 9) {
            finishMsg = getString(R.string.finish_msg_3);
        } else {
            finishMsg = getString(R.string.finish_msg_4);
        }

        finishMsg += "\n" + correctCount + " " + getString(R.string.out_of) + " " + questions.size();

        TextView finishText = findViewById(R.id.question_text);
        finishText.setText(finishMsg);

        LinearLayout summary = findViewById(R.id.summary);
        summary.removeAllViews();
        setVisibility(R.id.summary, View.VISIBLE);

        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);

            TextView text = new TextView(this, null, R.style.FullWidth);
            int padding = dpToPx(8);
            text.setPadding(padding, padding, padding, padding);

            String feedback = getString(R.string.question) + " " + (i + 1) + "\t\t\t";
            feedback += question.isCorrect() ? getString(R.string.correct) : getString(R.string.incorrect);

            text.setText(feedback);
            summary.addView(text);
        }

    }

    /**
     * Convert dp values into px values
     *
     * @param paddingDp dp to convert into px
     * @return int representing a px value
     */
    private int dpToPx(int paddingDp) {
        //https://stackoverflow.com/questions/4275797/view-setpadding-accepts-only-in-px-is-there-anyway-to-setpadding-in-dp
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (paddingDp * scale + 0.5f);
    }

    /**
     * Display a toast message
     * <p>
     * If a toast is already being displayed, cancel it and display a new one.
     *
     * @param msg to display in the toast
     */
    private void showToast(String msg) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Load question data into memory
     */
    private void loadData() {
        this.questions = new ArrayList<>();

        Question q1 = new Question();
        q1.setText(getString(R.string.q1));
        q1.setType(QuestionType.SINGLE);
        q1.newAnswer(getString(R.string.q1a1), false);
        q1.newAnswer(getString(R.string.q1a2), false);
        q1.newAnswer(getString(R.string.q1a3), true);
        q1.newAnswer(getString(R.string.q1a4), false);
        questions.add(q1);

        Question q2 = new Question();
        q2.setText(getString(R.string.q2));
        q2.setType(QuestionType.MULTIPLE);
        q2.newAnswer(getString(R.string.q2a1), false);
        q2.newAnswer(getString(R.string.q2a2), true);
        q2.newAnswer(getString(R.string.q2a3), false);
        q2.newAnswer(getString(R.string.q2a4), true);
        questions.add(q2);

        Question q3 = new Question();
        q3.setText(getString(R.string.q3));
        q3.setType(QuestionType.SINGLE);
        q3.newAnswer(getString(R.string.q3a1), false);
        q3.newAnswer(getString(R.string.q3a2), false);
        q3.newAnswer(getString(R.string.q3a3), true);
        q3.newAnswer(getString(R.string.q3a4), false);
        questions.add(q3);

        Question q4 = new Question();
        q4.setText(getString(R.string.q4));
        q4.setType(QuestionType.MULTIPLE);
        q4.newAnswer(getString(R.string.q4a1), true);
        q4.newAnswer(getString(R.string.q4a2), false);
        q4.newAnswer(getString(R.string.q4a3), true);
        q4.newAnswer(getString(R.string.q4a4), false);
        questions.add(q4);

        Question q5 = new Question();
        q5.setText(getString(R.string.q5));
        q5.setType(QuestionType.FREETEXT);
        q5.newAnswer(getString(R.string.q5a1), true);
        questions.add(q5);

        Question q6 = new Question();
        q6.setText(getString(R.string.q6));
        q6.setType(QuestionType.SINGLE);
        q6.newAnswer(getString(R.string.q6a1), false);
        q6.newAnswer(getString(R.string.q6a2), true);
        questions.add(q6);

        Question q7 = new Question();
        q7.setText(getString(R.string.q7));
        q7.setType(QuestionType.FREETEXT);
        q7.newAnswer(getString(R.string.q7a1), true);
        questions.add(q7);

        Question q8 = new Question();
        q8.setText(getString(R.string.q8));
        q8.setType(QuestionType.SINGLE);
        q8.newAnswer(getString(R.string.q8a1), false);
        q8.newAnswer(getString(R.string.q8a2), true);
        questions.add(q8);

        Question q9 = new Question();
        q9.setText(getString(R.string.q9));
        q9.setType(QuestionType.MULTIPLE);
        q9.newAnswer(getString(R.string.q9a1), true);
        q9.newAnswer(getString(R.string.q9a2), true);
        q9.newAnswer(getString(R.string.q9a3), false);
        q9.newAnswer(getString(R.string.q9a4), false);
        questions.add(q9);
    }
}
