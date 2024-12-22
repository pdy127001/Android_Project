package com.example.project4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MathQuizActivity extends AppCompatActivity {
// 간단한 연산게임
    private Button btnSubmitAnswer, btnClear;
    private LinearLayout gameLayout;
    private TextView tvQuestion, tvScore, tvTimer;
    private DrawingView drawView;
    private String name="간단한 연산 게임";
    private int correctAnswer;
    private int score = 0;
    private CountDownTimer timer;
    private MediaPlayer correctSound, incorrectSound;
    private OCRHelper ocrHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mathquiz);
        // 정답 및 오답 소리 로드
        correctSound = MediaPlayer.create(this, R.raw.correct_sound);
        incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound);
        // View 초기화
        gameLayout = findViewById(R.id.gameLayout);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvScore = findViewById(R.id.tvScore);
        tvTimer = findViewById(R.id.tvTimer);
        drawView = findViewById(R.id.drawView);
        btnSubmitAnswer = findViewById(R.id.btnSubmitAnswer);
        btnClear = findViewById(R.id.btnClear);

        // OCR 초기화
        try {
            ocrHelper = new OCRHelper(this);
        } catch (Exception e) {
            Toast.makeText(this, "OCR 초기화 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
            ocrHelper = null; // 초기화 실패 시 null로 설정
        }

        // 정답 제출 버튼
        btnSubmitAnswer.setOnClickListener(v -> checkAnswer());

        // 지우기 버튼
        btnClear.setOnClickListener(v -> drawView.clearCanvas());

        // 바로 게임 시작
        startGame();
    }

    private void startGame() {
        Toast.makeText(this, "게임을 시작합니다!", Toast.LENGTH_SHORT).show();

        // 게임 레이아웃 표시
        gameLayout.setVisibility(View.VISIBLE);

        // 타이머 시작
        startTimer();

        // 첫 번째 문제 표시
        showNextQuestion();
    }

    private void startTimer() {
        timer = new CountDownTimer(60000, 1000) { // 1분 타이머 (60,000ms)
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("남은 시간: " + millisUntilFinished / 1000 + "초");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("시간 종료!");
                endGame();
            }
        };
        timer.start();
    }

    private void showNextQuestion() {
        // 답이 0부터 9 사이가 되도록 숫자 생성
        correctAnswer = (int) (Math.random() * 10); // 0부터 9 사이의 랜덤 정답
        int num1 = (int) (Math.random() * (correctAnswer + 1)); // num1은 0부터 correctAnswer까지
        int num2 = correctAnswer - num1; // num2는 정답에서 num1을 뺀 값

        tvQuestion.setText(num1 + " + " + num2 + " = ?");
    }

    private void checkAnswer() {
        if (ocrHelper == null) {
            Toast.makeText(this, "OCR이 초기화되지 않았습니다. 앱을 다시 실행해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap drawnBitmap = drawView.getBitmap(); // 사용자가 그린 그림 가져오기
        if (drawnBitmap == null) {
            Toast.makeText(this, "그림이 비어 있습니다. 다시 시도하세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        String recognizedText = ocrHelper.recognizeText(drawnBitmap);

        if (recognizedText == null || recognizedText.isEmpty()) {
            Toast.makeText(this, "숫자를 인식할 수 없습니다. 다시 시도하세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int userAnswer = Integer.parseInt(recognizedText.trim());
            if (userAnswer == correctAnswer) {
                score += 2; // 정답 시 점수 추가
                correctSound.start();
                Toast.makeText(this, "정답입니다!", Toast.LENGTH_SHORT).show();
            } else {
                incorrectSound.start();
                Toast.makeText(this, "오답입니다! 정답은 " + correctAnswer + " 입니다.", Toast.LENGTH_SHORT).show();
            }
            tvScore.setText("점수: " + score);
            drawView.clearCanvas();
            showNextQuestion();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "유효한 숫자가 아닙니다. 다시 시도하세요!", Toast.LENGTH_SHORT).show();
        }
    }

    private void endGame() {
        Intent intent = new Intent(MathQuizActivity.this, GameOverActivity.class);
        intent.putExtra("score", score); // 점수 전달
        intent.putExtra("name", name);  // 게임 이름 전달
        startActivity(intent); // GameOverActivity 시작
        finish(); // 현재 Activity 종료
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel(); // 타이머 정리
        }
    }
}