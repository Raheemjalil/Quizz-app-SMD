package com.example.myapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.graphics.Paint;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class Quiz extends AppCompatActivity {

    private TextView questionTextView;
    private RadioGroup optionsRadioGroup;
    private RadioButton optionButton1, optionButton2, optionButton3, optionButton4;
    private Button previousButton, nextButton, showAnswerButton;
    private TextView scoreTextView, timerTextView;
    private List<QuizQuestion> questionList;
    private int currentQuestionIndex = 0;
    private int userScore = 0;
    private boolean[] hasAnsweredQuestion; // Array to track answered questions
    private boolean[] hasShownAnswer; // Array to track if the answer has been shown
    private int[] correctAnswerIndices; // Array to store the correct answer indices
    private CountDownTimer quizTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        questionTextView = findViewById(R.id.question);
        optionsRadioGroup = findViewById(R.id.Buttons);
        optionButton1 = findViewById(R.id.Button1);
        optionButton2 = findViewById(R.id.Button2);
        optionButton3 = findViewById(R.id.Button3);
        optionButton4 = findViewById(R.id.Button4);
        previousButton = findViewById(R.id.prev);
        nextButton = findViewById(R.id.next);
        showAnswerButton = findViewById(R.id.showAnswerButton);
        scoreTextView = findViewById(R.id.Marks);
        timerTextView = findViewById(R.id.time);

        // Initialize your question list
        questionList = Arrays.asList(
                new QuizQuestion("What is 2 + 2?", new String[]{"3", "4", "5", "6"}, 1),
                new QuizQuestion("What is 7 - 3?", new String[]{"4", "5", "6", "7"}, 1),
                new QuizQuestion("What is 9 x 3?", new String[]{"27", "18", "36", "24"}, 0),
                new QuizQuestion("What is 81 ÷ 9?", new String[]{"7", "8", "9", "10"}, 2),
                new QuizQuestion("What is 12 + 15?", new String[]{"25", "27", "28", "30"}, 1),
                new QuizQuestion("What is 14 - 7?", new String[]{"5", "6", "7", "8"}, 3),
                new QuizQuestion("What is 5 x 6?", new String[]{"25", "30", "35", "40"}, 1),
                new QuizQuestion("What is 48 ÷ 8?", new String[]{"5", "6", "7", "8"}, 1),
                new QuizQuestion("What is 15 + 9?", new String[]{"22", "23", "24", "25"}, 2),
                new QuizQuestion("What is 100 - 40?", new String[]{"50", "60", "70", "80"}, 1),
                new QuizQuestion("What is 3 x 7?", new String[]{"18", "21", "24", "27"}, 1),
                new QuizQuestion("What is 56 ÷ 8?", new String[]{"6", "7", "8", "9"}, 1),
                new QuizQuestion("What is 13 + 29?", new String[]{"40", "41", "42", "43"}, 1),
                new QuizQuestion("What is 90 - 45?", new String[]{"40", "45", "50", "55"}, 2),
                new QuizQuestion("What is 8 x 5?", new String[]{"35", "40", "45", "50"}, 1),
                new QuizQuestion("What is 64 ÷ 8?", new String[]{"6", "7", "8", "9"}, 2),
                new QuizQuestion("What is 19 + 22?", new String[]{"39", "40", "41", "42"}, 0),
                new QuizQuestion("What is 72 - 30?", new String[]{"40", "42", "44", "46"}, 1),
                new QuizQuestion("What is 11 x 3?", new String[]{"32", "33", "34", "35"}, 1),
                new QuizQuestion("What is 100 ÷ 4?", new String[]{"20", "25", "30", "35"}, 1),
                new QuizQuestion("What is 24 + 16?", new String[]{"38", "40", "42", "44"}, 1),
                new QuizQuestion("What is 48 - 17?", new String[]{"29", "30", "31", "32"}, 1)
        );


        // Initialize the arrays
        hasAnsweredQuestion = new boolean[questionList.size()];
        hasShownAnswer = new boolean[questionList.size()];
        correctAnswerIndices = new int[questionList.size()];

        for (int i = 0; i < questionList.size(); i++) {
            correctAnswerIndices[i] = questionList.get(i).getCorrectAnswerIndex();
        }

        // Initialize and start the countdown timer
        startQuizTimer(5 * 60 * 1000); // 5 minutes in milliseconds

        // Display the first question
        displayCurrentQuestion();

        // Setup button listeners
        previousButton.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                displayCurrentQuestion();
            }
        });

        nextButton.setOnClickListener(v -> {
            evaluateAnswer();
            if (currentQuestionIndex >= questionList.size() - 2) {
                int answeredCount = 0;
                int shownCount = 0;
                for (int i = 0; i < questionList.size(); i++) {
                    if (hasAnsweredQuestion[i]) {
                        answeredCount++;
                    }
                    if (hasShownAnswer[i]) {
                        shownCount++;
                    }
                }

                if (currentQuestionIndex == questionList.size() - 2 && (answeredCount + shownCount) < (questionList.size() - 1)) {
                    Toast.makeText(Quiz.this, "Finish quiz before exiting.", Toast.LENGTH_SHORT).show();
                } else {
                    if (currentQuestionIndex >= questionList.size() - 2) {
                        // Show completion screen when the last question is answered
                        showQuizCompletionScreen();
                    } else {
                        currentQuestionIndex++;
                        displayCurrentQuestion();
                    }
                }
            } else {
                currentQuestionIndex++;
                displayCurrentQuestion();
            }
        });

        showAnswerButton.setOnClickListener(v -> revealAnswer());
    }

    private void showQuizCompletionScreen() {
        if (quizTimer != null) {
            quizTimer.cancel();
        }

        questionTextView.setText("Quiz Finished!");

        optionsRadioGroup.setVisibility(View.GONE);
        previousButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        showAnswerButton.setVisibility(View.GONE);

        scoreTextView.setText("Final score: " + userScore);

        Button exitButton = new Button(this);
        exitButton.setText("Exit");
        exitButton.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        ));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) exitButton.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.Marks);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        exitButton.setLayoutParams(params);

        ((RelativeLayout) findViewById(R.id.quizLayout)).addView(exitButton);

        exitButton.setOnClickListener(v -> finish());
    }

    private void startQuizTimer(long millisInFuture) {
        quizTimer = new CountDownTimer(millisInFuture, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerTextView.setText(String.format("Time: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerTextView.setText("Time: 00:00");
                endQuiz();
            }
        }.start();
    }

    private void endQuiz() {
        if (quizTimer != null) {
            quizTimer.cancel();
        }

        optionButton1.setEnabled(false);
        optionButton2.setEnabled(false);
        optionButton3.setEnabled(false);
        optionButton4.setEnabled(false);
        previousButton.setEnabled(false);
        nextButton.setEnabled(false);
        showAnswerButton.setEnabled(false);

        Toast.makeText(this, "Quiz Completed! Your final score is: " + userScore, Toast.LENGTH_LONG).show();
    }

    private void displayCurrentQuestion() {
        QuizQuestion currentQuestion = questionList.get(currentQuestionIndex);
        questionTextView.setText(currentQuestion.getQuestion());

        String[] options = currentQuestion.getOptionslist();
        optionButton1.setText(options[0]);
        optionButton2.setText(options[1]);
        optionButton3.setText(options[2]);
        optionButton4.setText(options[3]);

        optionsRadioGroup.clearCheck();
        resetOptionTextColors();

        boolean isQuestionAnswered = hasAnsweredQuestion[currentQuestionIndex];
        optionButton1.setEnabled(!isQuestionAnswered);
        optionButton2.setEnabled(!isQuestionAnswered);
        optionButton3.setEnabled(!isQuestionAnswered);
        optionButton4.setEnabled(!isQuestionAnswered);

        if (isQuestionAnswered) {
            highlightCorrectAnswer();
        }

        showAnswerButton.setEnabled(!isQuestionAnswered && !hasShownAnswer[currentQuestionIndex]);
        previousButton.setEnabled(currentQuestionIndex > 0);
        nextButton.setEnabled(currentQuestionIndex < questionList.size() - 1);

        updateScoreDisplay();
    }

    private void evaluateAnswer() {
        int selectedRadioButtonId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            int selectedOptionIndex = optionsRadioGroup.indexOfChild(selectedRadioButton);

            QuizQuestion currentQuestion = questionList.get(currentQuestionIndex);
            if (selectedOptionIndex == currentQuestion.getCorrectAnswerIndex()) {
                userScore += 5;
            } else {
                userScore--;
            }

            hasAnsweredQuestion[currentQuestionIndex] = true;
            highlightCorrectAnswer();
        }
    }

    private void revealAnswer() {
        highlightCorrectAnswer();

        optionButton1.setEnabled(false);
        optionButton2.setEnabled(false);
        optionButton3.setEnabled(false);
        optionButton4.setEnabled(false);

        userScore--;
        hasAnsweredQuestion[currentQuestionIndex] = true;
        hasShownAnswer[currentQuestionIndex] = true;

        updateScoreDisplay();
        showAnswerButton.setEnabled(false);
    }

    private void highlightCorrectAnswer() {
        int correctAnswerIndex = correctAnswerIndices[currentQuestionIndex];
        RadioButton correctOptionButton = (RadioButton) optionsRadioGroup.getChildAt(correctAnswerIndex);
        applyUnderlineToTextView(correctOptionButton);
    }

    private void resetOptionTextColors() {
        optionButton1.setPaintFlags(optionButton1.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        optionButton2.setPaintFlags(optionButton2.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        optionButton3.setPaintFlags(optionButton3.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        optionButton4.setPaintFlags(optionButton4.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
    }

    private void updateScoreDisplay() {
        scoreTextView.setText("Score: " + userScore);
    }

    private void applyUnderlineToTextView(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}
