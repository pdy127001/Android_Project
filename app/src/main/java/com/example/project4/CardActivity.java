package com.example.project4;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class CardActivity extends AppCompatActivity {
    private MediaPlayer correctSound, incorrectSound;
    private GridLayout gridLayout;
    private ArrayList<String> cardValues;
    private Button firstCard, secondCard;
    private boolean isBusy = false;
    private int matchedPairs = 0;
    private int score = 0;
    private TextView timerTextView, scoreTextView;
    private CountDownTimer timer;
    String name="카드 뒤집기 게임";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card);
// 정답 및 오답 소리 로드
        correctSound = MediaPlayer.create(this, R.raw.correct_sound);
        incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound);
        gridLayout = findViewById(R.id.gridLayout);
        timerTextView = findViewById(R.id.timerTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        cardValues = new ArrayList<>();

        String[] operations = {"9-8", "4/2", "9/3", "2*2", "6-1", "3*2", "4+3", "7+1"};
        String[] answers = {"1", "2", "3", "4", "5", "6", "7", "8"};
        for (int i = 0; i < operations.length; i++) {
            cardValues.add(operations[i]);
            cardValues.add(answers[i]);
        }
        Collections.shuffle(cardValues);

        setUpGameBoard();
        startTimer();
    }

    private void setUpGameBoard() {
        int totalCards = 16;
        int columnCount = 4;

        for (int i = 0; i < totalCards; i++) {
            final Button button = new Button(this);
            button.setText("?"); // 초기 텍스트
            button.setTag(cardValues.get(i));

            // 카드 스타일 설정
            button.setBackgroundColor(getResources().getColor(android.R.color.white)); // 배경 흰색
            button.setTextSize(18);
            button.setTextColor(getResources().getColor(android.R.color.black)); // 텍스트 색상을 검은색으로 설정
            button.setPadding(8, 8, 8, 8);

            // GridLayout 매개변수 설정
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % columnCount, 1f);
            params.rowSpec = GridLayout.spec(i / columnCount, 1f);
            params.setMargins(8, 8, 8, 8); // 카드 간격
            button.setLayoutParams(params);

            // 버튼 클릭 리스너 추가
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isBusy) return;

                    Button clickedButton = (Button) v;
                    clickedButton.setText((String) v.getTag());

                    if (firstCard == null) {
                        firstCard = clickedButton;
                    } else {
                        secondCard = clickedButton;
                        isBusy = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                checkMatch();
                            }
                        }, 1000);
                    }
                }
            });
            gridLayout.addView(button); // 버튼 추가
        }
    }

    private void checkMatch() {
        String firstValue = (String) firstCard.getTag();
        String secondValue = (String) secondCard.getTag();

        if (isMatchingPair(firstValue, secondValue)) {
            firstCard.setEnabled(false);
            secondCard.setEnabled(false);
            correctSound.start();  // 맞을 때 소리
            matchedPairs++;
            score += 10;
            scoreTextView.setText("Score: " + score);

            if (matchedPairs == 8) {
                timer.cancel();
                revealAllCards();
                showEndGameDialog("Congratulations! You've matched all pairs!");
            }
        } else {
            incorrectSound.start();
            firstCard.setText("?");
            secondCard.setText("?");
        }

        firstCard = null;
        secondCard = null;
        isBusy = false;
    }

    private boolean isMatchingPair(String value1, String value2) {

        return (value1.equals("9-8") && value2.equals("1")) || (value1.equals("1") && value2.equals("9-8")) ||
                (value1.equals("4/2") && value2.equals("2")) || (value1.equals("2") && value2.equals("4/2")) ||
                (value1.equals("9/3") && value2.equals("3")) || (value1.equals("3") && value2.equals("9/3")) ||
                (value1.equals("2*2") && value2.equals("4")) || (value1.equals("4") && value2.equals("2*2")) ||
                (value1.equals("6-1") && value2.equals("5")) || (value1.equals("5") && value2.equals("6-1")) ||
                (value1.equals("3*2") && value2.equals("6")) || (value1.equals("6") && value2.equals("3*2")) ||
                (value1.equals("4+3") && value2.equals("7")) || (value1.equals("7") && value2.equals("4+3")) ||
                (value1.equals("7+1") && value2.equals("8")) || (value1.equals("8") && value2.equals("7+1"));
    }

    private void startTimer() {
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("Time Left: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                revealAllCards();
                showEndGameDialog("Time's up! Game over!");
            }
        }.start();
    }

    private void revealAllCards() {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button button = (Button) gridLayout.getChildAt(i);
            button.setText((String) button.getTag());
        }
    }

    private void showEndGameDialog(String message) {
        Intent intent = new Intent(CardActivity.this, GameOverActivity.class);
        intent.putExtra("score", score); // 점수 전달
        intent.putExtra("name",name);
        startActivity(intent);
        finish();
    }
}