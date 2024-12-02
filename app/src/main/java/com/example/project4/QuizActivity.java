package com.example.project4;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private TextView timerText, scoreText, questionText;
    private ProgressBar questionTimerBar;
    private Button[] answerButtons;
    private int score = 0;
    private final long totalGameTime = 60000; // 전체 타이머: 60초
    private final long questionTime = 9000; // 문제당 타이머: 9초
    private CountDownTimer gameTimer;
    private CountDownTimer questionTimer;
    private Random random = new Random();
    private String name="상식 퀴즈 게임";
    // 상식 문제 세트
    private String[][] questions = {
            {"청동기 문화를 바탕으로 등장한 우리나라 최초의 국가는?", "신라", "조선", "고조선", "대한민국"},
            {"세계의 5대양 중 가장 큰 바다는?", "대평양", "태평양", "대서양", "황해"},
            {"세계 인구의 절반 이상이 살고 있는 가장 큰 대륙은?", "유럽", "러시아", "남아메리카", "아시아"},
            {"어떤 장소에서 서로 영향을 주고 받는 생물과 환경 전체를 무엇이라고 할까?", "삶", "인생", "생태계", "상호작용"},
            {"우리나라 최고의 법으로 여러 법을 만드는 바탕이 되는 이것은?", "국회", "헌법", "근본", "형법"},
            {"10월 9일은 무슨 날인가?", "국회의 날", "한자의 날", "한글날", "한국인의 날"},
            {"삼국시대에서 가장 전성기가 빨랐던 나라는?", "백제", "고구려", "신라", "가야"},
            {"어머니의 할머니를 일컫는 말은?", "엄마!", "조부", "증조할머니", "고조할머니"},
            {"세계 최초 문명은?", "메소포타미아", "인더스", "고구려", "황허"},
            {"발해를 건국한 사람 이름은?", "장수왕", "문무왕", "이순신", "대조영"}
    };

    // 각 질문의 정답 인덱스
    private int[] correctAnswers = {2, 1, 3, 2, 1, 2, 0, 2, 0, 3};
    private Button startGameButton;
    private Drawable defaultButtonBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz);

        timerText = findViewById(R.id.timerText);
        scoreText = findViewById(R.id.scoreText);
        questionTimerBar = findViewById(R.id.questionTimerBar);
        questionText = findViewById(R.id.questionText);
        startGameButton = findViewById(R.id.startGameButton);

        answerButtons = new Button[]{
                findViewById(R.id.answerButton1),
                findViewById(R.id.answerButton2),
                findViewById(R.id.answerButton3),
                findViewById(R.id.answerButton4)
        };

        // 첫 번째 버튼의 기본 배경 저장
        defaultButtonBackground = answerButtons[0].getBackground();

        // 게임 시작 버튼 클릭 시 게임 시작
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameButton.setVisibility(View.GONE); // 게임 시작 버튼 숨기기
                showGameUI(); // 게임 UI 요소들 표시
                startGame();
            }
        });
    }


    private void showGameUI() {
        timerText.setVisibility(View.VISIBLE);
        scoreText.setVisibility(View.VISIBLE);
        questionTimerBar.setVisibility(View.VISIBLE);
        questionText.setVisibility(View.VISIBLE);
        for (Button button : answerButtons) {
            button.setVisibility(View.VISIBLE); // 버튼 모두 표시
        }
    }

    private void startGame() {
        score = 0;
        updateScore();
        startGameTimer();
        loadNewQuestion();
    }

    private void startGameTimer() {
        gameTimer = new CountDownTimer(totalGameTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                showEndGameDialog();
//                showFinalScoreDialog();
            }
        }.start();
    }

    private void loadNewQuestion() {
        int questionIndex = random.nextInt(questions.length);
        questionText.setText(questions[questionIndex][0]);

        for (int i = 0; i < answerButtons.length; i++) {
            Button button = answerButtons[i];
            button.setVisibility(View.VISIBLE); // 버튼을 항상 표시되도록 설정
            button.setText(questions[questionIndex][i + 1]);
            // 배경색을 기본 배경으로 설정
            button.setBackground(defaultButtonBackground);
            button.setEnabled(true); // 버튼 활성화

            final boolean isCorrect = (i == correctAnswers[questionIndex]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAnswerSelected(isCorrect, (Button) v);
                }
            });
        }
        startQuestionTimer();
    }


    private void startQuestionTimer() {
        questionTimerBar.setProgress(9);
        questionTimer = new CountDownTimer(questionTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                questionTimerBar.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                loadNewQuestion();
            }
        }.start();
    }

    private void onAnswerSelected(boolean isCorrect, final Button selectedButton) {
        questionTimer.cancel(); // 정답 선택 시 타이머 중지
        if (isCorrect) {
            score += 10;
            updateScore();
            playSound(R.raw.correct_sound); // 정답 효과음
            //selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light)); // 정답 선택 시 배경색 설정
        } else {
            playSound(R.raw.incorrect_sound); // 오답 효과음
           // selectedButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark)); // 오답 선택 시 배경색 설정
        }

        // 버튼이 사라지지 않도록 모든 버튼을 VISIBLE로 설정하고 배경색 초기화
        for (Button button : answerButtons) {
            button.setVisibility(View.VISIBLE); // 버튼을 다시 보이게 설정
            button.setEnabled(false); // 일시적으로 비활성화하여 중복 클릭 방지
        }

        // 1초 후에 버튼의 배경색 초기화 및 새 질문 로드
        selectedButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Button button : answerButtons) {
                    button.setVisibility(View.VISIBLE); // 모든 버튼을 다시 표시
                    button.setBackground(defaultButtonBackground); // 배경색 초기화 (기본 배경으로 설정)
                    button.setEnabled(true); // 버튼을 다시 활성화
                }
                loadNewQuestion();
            }
        }, 1000);
    }

    private void updateScore() {
        scoreText.setText("Score: " + score);
    }

    private void playSound(int soundResource) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResource);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    private void showEndGameDialog() {
        Intent intent = new Intent(QuizActivity.this, GameOverActivity.class);
        intent.putExtra("score", score); // 점수 전달
        intent.putExtra("name",name);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameTimer != null) gameTimer.cancel();
        if (questionTimer != null) questionTimer.cancel();
    }
}