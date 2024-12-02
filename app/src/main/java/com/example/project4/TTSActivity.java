package com.example.project4;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.util.Random;

public class TTSActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private TextView timerTextView, scoreTextView, correctAnswerTextView;
    private EditText inputEditText;
    private Button submitButton, replayButton, startButton;
    private ImageView resultImageView;
    private LinearLayout difficultyLayout;
    private Button easyButton, mediumButton, hardButton;
    private CountDownTimer gameTimer;
    private int score = 0;
    private String currentText = "";
    private String difficultyLevel = "";  // 난이도 설정
    private MediaPlayer correctSound, incorrectSound;
    private String name="받아쓰기 게임";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tts);

        timerTextView = findViewById(R.id.timerTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        correctAnswerTextView = findViewById(R.id.correctAnswerTextView);
        inputEditText = findViewById(R.id.inputEditText);
        submitButton = findViewById(R.id.submitButton);
        resultImageView = findViewById(R.id.resultImageView);
        replayButton = findViewById(R.id.replayButton);
        startButton = findViewById(R.id.startButton);
        difficultyLayout = findViewById(R.id.difficultyLayout);

        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        hardButton = findViewById(R.id.hardButton);

        // TTS 초기화
        tts = new TextToSpeech(this, this);

        // 정답 및 오답 소리 로드
        correctSound = MediaPlayer.create(this, R.raw.correct_sound);
        incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound);

        // 게임 시작 버튼 클릭 시 난이도 선택 버튼 표시
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(View.GONE);  // 게임 시작 버튼 숨기기
                difficultyLayout.setVisibility(View.VISIBLE);  // 난이도 선택 버튼 표시
            }
        });

        // 난이도 선택 버튼 클릭 시 난이도를 설정하고 게임 시작
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficultyLevel = "easy";
                startGame();
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficultyLevel = "medium";
                startGame();
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficultyLevel = "hard";
                startGame();
            }
        });

        // 제출 버튼 클릭 시 답 확인
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        // 다시 듣기 버튼 클릭 시 현재 문제 다시 듣기
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replayCurrentText();
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.ENGLISH);
        }
    }

    private void startGame() {
        // 난이도 버튼 숨기기 및 다른 UI 요소 표시
        difficultyLayout.setVisibility(View.GONE);

        timerTextView.setVisibility(View.VISIBLE);
        scoreTextView.setVisibility(View.VISIBLE);
        inputEditText.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        replayButton.setVisibility(View.VISIBLE);

        // 점수 초기화 및 타이머 시작
        score = 0;
        scoreTextView.setText("점수: " + score);
        startGameTimer();
        speakRandomText();
    }

    private void startGameTimer() {
        gameTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("남은 시간: " + millisUntilFinished / 1000 + "초");
            }

            @Override
            public void onFinish() {
                timerTextView.setText("시간 종료!");
                submitButton.setEnabled(false);
                replayButton.setEnabled(false);
                tts.stop();

                // 최종 점수를 알림 대화상자로 표시
                showEndGameDialog();
            }
        }.start();
    }

    private void speakRandomText() {
        String[] sentences = {
                "Hello world, this is an example sentence.",
                "Android programming is fun and exciting.",
                "Text to speech conversion in Android is a useful feature.",
                "This is a sample sentence for demonstration.",
                "Keep going and learning new things every day.",
                "Java is a versatile programming language.",
                "Artificial intelligence is transforming the world.",
                "Never stop exploring and growing.",
                "Success comes with persistence and hard work.",
                "Reading is to the mind what exercise is to the body."
        };

        String[] phrases = {
                "Hello world",
                "Android programming",
                "Text to speech",
                "Sample sentence",
                "Keep going",
                "Versatile programming",
                "Artificial intelligence",
                "Exploring and growing",
                "Persistence and hard work",
                "Mind and body exercise"
        };

        String[] words = {
                "Hello",
                "Android",
                "Text",
                "Sample",
                "Keep",
                "Java",
                "Artificial",
                "Success",
                "Reading",
                "Exercise"
        };


        Random random = new Random();

        // 난이도에 따라 다른 유형의 문제 출제
        switch (difficultyLevel) {
            case "hard":
                currentText = sentences[random.nextInt(sentences.length)];
                break;
            case "medium":
                currentText = phrases[random.nextInt(phrases.length)];
                break;
            case "easy":
                currentText = words[random.nextInt(words.length)];
                break;
        }

        tts.speak(currentText, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void replayCurrentText() {
        // 현재 텍스트 다시 듣기
        tts.speak(currentText, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void checkAnswer() {
        String userInput = inputEditText.getText().toString();

        // 일치 여부 확인 및 이미지 표시
        if (userInput.equals(currentText)) {
            score += 10;
            resultImageView.setImageResource(R.drawable.correct);  // 맞을 때 이미지
            correctSound.start();  // 맞을 때 소리
        } else {
            resultImageView.setImageResource(R.drawable.incorrect); // 틀릴 때 이미지
            incorrectSound.start();  // 틀릴 때 소리
        }

        // 점수 및 정답 표시 업데이트
        scoreTextView.setText("점수: " + score);
        correctAnswerTextView.setText("정답: " + currentText);
        resultImageView.setVisibility(View.VISIBLE);
        correctAnswerTextView.setVisibility(View.VISIBLE);

        // 2초 대기 후 다음 문장 출력
        inputEditText.setText("");
        inputEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                resultImageView.setVisibility(View.GONE);
                correctAnswerTextView.setVisibility(View.GONE);
                speakRandomText();
            }
        }, 2000);
    }

    private void showEndGameDialog() {
        Intent intent = new Intent(TTSActivity.this, GameOverActivity.class);
        intent.putExtra("score", score); // 점수 전달
        intent.putExtra("name",name);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        if (correctSound != null) {
            correctSound.release();
        }
        if (incorrectSound != null) {
            incorrectSound.release();
        }
        super.onDestroy();
    }
}
