package com.developer_abdul_halim.spoken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionCollection extends AppCompatActivity {
    RadioGroup radioGroup;
    TextView lblQuestion, timerTextView;
    RadioButton optionA, optionB, optionC, optionD;
    Button confirm;
    int score;
    String rightAnswer, Answer;
    public static List<QuestionModule> question_list = new ArrayList<>();
    private CountDownTimer timer;
    private QuestionModule currentQuestion;

    private TextView questionCounter;
    private int totalQuestion = 0;
    private int currentIndex = 0;


    ProgressDialog progressDialog;

    RequestQueue requestQueue;
    JsonArrayRequest jsonArrayRequest;

    public static String JSONurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        radioGroup = findViewById(R.id.radioGroup);
        lblQuestion = findViewById(R.id.lblPergunta);
        optionA = findViewById(R.id.opcaoA);
        optionB = findViewById(R.id.opcaoB);
        optionC = findViewById(R.id.opcaoC);
        optionD = findViewById(R.id.opcaoD);
        confirm = findViewById(R.id.confirm);
        questionCounter = findViewById(R.id.questionCounter);
        timerTextView = findViewById(R.id.timer);
        score = 0;


//===========================================================================
        progressDialog = new ProgressDialog(QuestionCollection.this);
        progressDialog.setMessage(" কুইজ লোড হচ্ছে!! দয়া করে অপেক্ষা করুন! ");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        //==================================================================================


        createQuestionBank(this);
    }


    private void loadQuestion() {
        Answer = null;
        if (question_list.size() == 0) {
            updateCategoryCompletion();
            Intent intent = new Intent(this, ScoreActivity.class);
            intent.putExtra("score", score);
            intent.putExtra("total", totalQuestion);
            startActivity(intent);
            question_list.clear();
            finish();
            return;
        }

        updateQuestionCounter();
        resetOptionsBackground(); // Reset the backgrounds
        resetTimer(); // Start/reset the timer for the new question

        currentQuestion = question_list.remove(0);  // Set the current question here

        lblQuestion.setText(currentQuestion.getQuestion());
        List<String> answers = currentQuestion.getAnswers();

        optionA.setText(answers.get(0));
        optionB.setText(answers.get(1));
        optionC.setText(answers.get(2));
        optionD.setText(answers.get(3));
        rightAnswer = currentQuestion.getRightAnswer();
    }


    //lock unlocking method=================================
    private final List<String> quizUrls = Arrays.asList(
            "https://developerabdulhalim.xyz/quiz_app/quiz.json",
            "https://developerabdulhalim.xyz/quiz_app/quiztwo.json",
            "https://developerabdulhalim.xyz/quiz_app/quizthree.json",
            "https://developerabdulhalim.xyz/quiz_app/quiz_four.json",
            "https://developerabdulhalim.xyz/quiz_app/quiz_five.json",
            "https://developerabdulhalim.xyz/quiz_app/quiz_six.json",
            "https://developerabdulhalim.xyz/quiz_app/quiz_seven.json",
            "https://developerabdulhalim.xyz/quiz_app/quiz_eight.json",
            "https://developerabdulhalim.xyz/quiz_app/quiz_nine.json",
            "https://developerabdulhalim.xyz/quiz_app/quiz_ten.json"
            // Add other URLs as needed
    );

    private void updateCategoryCompletion() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int categoryIndex = quizUrls.indexOf(QuestionCollection.JSONurl);
        if (categoryIndex != -1) {
            editor.putBoolean("Category" + categoryIndex + "Completed", true);
            editor.putLong("Category" + categoryIndex + "CompletionTime", System.currentTimeMillis());

            if (categoryIndex + 1 < quizUrls.size()) {
                editor.putBoolean("Category" + (categoryIndex + 1) + "Completed", true);
                editor.putLong("Category" + (categoryIndex + 1) + "CompletionTime", System.currentTimeMillis());
            }

            editor.apply();
        }
    }


    public void loadAnswer(View view) {
        int op = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedOption = findViewById(op);

        if (op == R.id.opcaoA) {
            Answer = "A";
        } else if (op == R.id.opcaoB) {
            Answer = "B";
        } else if (op == R.id.opcaoC) {
            Answer = "C";
        } else if (op == R.id.opcaoD) {
            Answer = "D";
        } else {
            return;
        }

        radioGroup.clearCheck();
        if (Answer.equals(rightAnswer)) {
            this.score += 1;
            selectedOption.setBackground(getResources().getDrawable(R.drawable.option_right));
            currentQuestion.setAnsweredCorrectly(true);
            MediaPlayer audio = MediaPlayer.create(this, R.raw.right_sound);
            audio.start();
        } else {
            selectedOption.setBackground(getResources().getDrawable(R.drawable.option_wrong));
            showCorrectAnswer();
            MediaPlayer audio = MediaPlayer.create(this, R.raw.wrong);
            audio.start();
        }


        currentIndex++;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadQuestion();
            }
        }, 1000);
    }


    private void showCorrectAnswer() {
        switch (rightAnswer) {
            case "A":
                optionA.setBackground(getResources().getDrawable(R.drawable.option_right));
                break;
            case "B":
                optionB.setBackground(getResources().getDrawable(R.drawable.option_right));
                break;
            case "C":
                optionC.setBackground(getResources().getDrawable(R.drawable.option_right));
                break;
            case "D":
                optionD.setBackground(getResources().getDrawable(R.drawable.option_right));
                break;
        }
    }


    private void resetOptionsBackground() {
        optionA.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        optionB.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        optionC.setBackground(getResources().getDrawable(R.drawable.option_unselected));
        optionD.setBackground(getResources().getDrawable(R.drawable.option_unselected));
    }


    private void resetTimer() {
        if (timer != null) {
            timer.cancel(); // Cancel the previous timer if it was running
        }

        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Handle what happens when the timer finishes
                if (Answer == null) {
                    Toast.makeText(QuestionCollection.this, "Time's Out!", Toast.LENGTH_SHORT).show();
                    loadQuestion();
                } else {
                    checkAnswer(null);
                }

            }
        }.start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Cancel any running handlers or timers here
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure the timer is cancelled to avoid potential leaks.
        if (timer != null) {
            timer.cancel();
        }
    }


    private void checkAnswer(View view) {
        if (timer != null) {
            timer.cancel(); // Cancel the timer when checking the answer
        }

        if (Answer.equals(rightAnswer)) {
            this.score += 1;
            Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong Answer!", Toast.LENGTH_SHORT).show();
        }

        loadQuestion(); // Continue to the next question
    }


    private void updateQuestionCounter() {
        questionCounter.setText(String.format("%d/%d", (currentIndex + 1), totalQuestion));

    }


    public void createQuestionBank(Context context) {
        question_list.clear();

         jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSONurl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String question = jsonObject.getString("question");
                                String rightAnswer = jsonObject.getString("rightAnswer");
                                String answer1 = jsonObject.getString("answer1");
                                String answer2 = jsonObject.getString("answer2");
                                String answer3 = jsonObject.getString("answer3");
                                String answer4 = jsonObject.getString("answer4");
                                QuestionModule q = new QuestionModule(question, rightAnswer, answer1, answer2, answer3, answer4);
                                question_list.add(q);
                            }
                            // Load the first question after the questions have been fetched
                            totalQuestion = question_list.size();
                            loadQuestion();

                            progressDialog.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NoInternet(QuestionCollection.this);
                        progressDialog.dismiss();
                    }
                });

        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }


    private void NoInternet(Activity activity) {

        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        View mView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
        Button cancleBTN = mView.findViewById(R.id.cancleBTN);
        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCancelable(false);

        cancleBTN.setText("exit?");
        mView.findViewById(R.id.cancleBTN).setOnClickListener(v -> {
            activity.finishAffinity();
            alertDialog.dismiss();
        });

        mView.findViewById(R.id.okBTN).setOnClickListener(v -> {
            requestQueue = Volley.newRequestQueue(activity);
            requestQueue.add(jsonArrayRequest);
            alertDialog.dismiss();
        });
        alertDialog.show();
    }//=================================================================================


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        question_list.clear();
        finish();
    }


}

