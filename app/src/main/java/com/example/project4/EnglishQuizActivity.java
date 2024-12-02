package com.example.project4;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class EnglishQuizActivity extends AppCompatActivity {
    private String name="영어 퀴즈 게임";
    private MediaPlayer correctSound, incorrectSound;
    public class Question {
        private String word;
        private String meaning;

        public Question(String word, String meaning) {
            this.word = word;
            this.meaning = meaning;
        }

        public String getWord() {
            return word;
        }

        public String getMeaning() {
            return meaning;
        }
    }

    private TextView scoreTextView, questionTextView, timerTextView, finalScoreTextView, wrongAnswersTextView;
    private EditText userInputEditText;
    private Button submitButton, easyButton, mediumButton, hardButton, showWrongAnswersButton;

    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;
    private CountDownTimer countDownTimer;
    private boolean isGameActive = false;
    private List<String> wrongAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.englishquiz);
// 정답 및 오답 소리 로드
        correctSound = MediaPlayer.create(this, R.raw.correct_sound);
        incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound);
        // 각 뷰 초기화
        scoreTextView = findViewById(R.id.scoreTextView);
        questionTextView = findViewById(R.id.questionTextView);
        userInputEditText = findViewById(R.id.userInputEditText);
        submitButton = findViewById(R.id.submitButton);
        timerTextView = findViewById(R.id.timerTextView);
        easyButton = findViewById(R.id.easyButton);
        mediumButton = findViewById(R.id.mediumButton);
        hardButton = findViewById(R.id.hardButton);
        showWrongAnswersButton = findViewById(R.id.showWrongAnswersButton);
        finalScoreTextView = findViewById(R.id.finalScoreTextView);
        wrongAnswersTextView = findViewById(R.id.wrongAnswersTextView);

        easyButton.setOnClickListener(v -> startGame("easy"));
        mediumButton.setOnClickListener(v -> startGame("medium"));
        hardButton.setOnClickListener(v -> startGame("hard"));

        submitButton.setOnClickListener(v -> checkAnswer());
        showWrongAnswersButton.setOnClickListener(v -> showWrongAnswers());

        showWrongAnswersButton.setVisibility(View.GONE); // 처음에는 숨김
    }

    private void startGame(String difficulty) {
        // 게임이 이미 진행 중일 경우, 종료하지 않도록 방지
        if (isGameActive) {
            return; // 게임이 이미 진행 중이면 아무 작업도 하지 않음
        }

        isGameActive = true;
        score = 0;
        currentQuestionIndex = 0;
        wrongAnswers.clear();
        scoreTextView.setText("점수: " + score);
        timerTextView.setText("시간: 60");
        userInputEditText.setText("");
        questionTextView.setVisibility(View.VISIBLE); // 질문 텍스트뷰 보이기
        finalScoreTextView.setVisibility(View.GONE); // 최종 점수 숨기기
        wrongAnswersTextView.setVisibility(View.GONE); // 틀린 답변 텍스트뷰 숨기기
        showWrongAnswersButton.setVisibility(View.GONE); // 게임 시작 시 버튼 숨김

        // 타이머 설정
        if (countDownTimer != null) {
            countDownTimer.cancel(); // 이전 타이머가 있으면 취소
        }

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText("시간: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                isGameActive = false;
                endGame(); // 게임 종료 처리
            }
        }.start();

        setQuestions(difficulty);
        setNewQuestion();
    }

    private void setQuestions(String difficulty) {
        questions.clear();

        if (difficulty.equals("easy")) {
            questions.add(new Question("Cat", "고양이"));
            questions.add(new Question("Dog", "개"));
            questions.add(new Question("Fish", "물고기"));
            questions.add(new Question("Bird", "새"));
            questions.add(new Question("Apple", "사과"));
            questions.add(new Question("Ball", "공"));
            questions.add(new Question("House", "집"));
            questions.add(new Question("Tree", "나무"));
            questions.add(new Question("Car", "자동차"));
            questions.add(new Question("Book", "책"));
            questions.add(new Question("Sun", "태양"));
            questions.add(new Question("Moon", "달"));
            questions.add(new Question("Star", "별"));
            questions.add(new Question("Flower", "꽃"));
            questions.add(new Question("Grass", "풀"));
            questions.add(new Question("Milk", "우유"));
            questions.add(new Question("Water", "물"));
            questions.add(new Question("Bread", "빵"));
            questions.add(new Question("Egg", "계란"));
            questions.add(new Question("Chair", "의자"));
            questions.add(new Question("Table", "테이블"));
            questions.add(new Question("Window", "창문"));
            questions.add(new Question("Door", "문"));
            questions.add(new Question("Shoe", "신발"));
            questions.add(new Question("Hat", "모자"));
            questions.add(new Question("Pencil", "연필"));
            questions.add(new Question("Pen", "펜"));
            questions.add(new Question("Paper", "종이"));
            questions.add(new Question("Toy", "장난감"));
            questions.add(new Question("Game", "게임"));
            questions.add(new Question("Friend", "친구"));
            questions.add(new Question("Family", "가족"));
            questions.add(new Question("Happy", "행복한"));
            questions.add(new Question("Sad", "슬픈"));
            questions.add(new Question("Angry", "화난"));
            questions.add(new Question("Big", "큰"));
            questions.add(new Question("Small", "작은"));
            questions.add(new Question("Fast", "빠른"));
            questions.add(new Question("Slow", "느린"));
            questions.add(new Question("Hot", "뜨거운"));
            questions.add(new Question("Cold", "차가운"));
            questions.add(new Question("Light", "빛"));
            questions.add(new Question("Dark", "어두운"));
            questions.add(new Question("Strong", "강한"));
            questions.add(new Question("Weak", "약한"));
            questions.add(new Question("Clean", "깨끗한"));
            questions.add(new Question("Dirty", "더러운"));
            questions.add(new Question("Rich", "부유한"));
            questions.add(new Question("Poor", "가난한"));
            questions.add(new Question("Young", "젊은"));
            questions.add(new Question("Old", "늙은"));
            questions.add(new Question("Smart", "똑똑한"));
            questions.add(new Question("Dumb", "멍청한"));
            questions.add(new Question("Brave", "용감한"));
            questions.add(new Question("Shy", "수줍은"));
        } else if (difficulty.equals("medium")) {
            questions.add(new Question("Adventure", "모험"));
            questions.add(new Question("Curious", "호기심이 많은"));
            questions.add(new Question("Difficult", "어려운"));
            questions.add(new Question("Exciting", "신나는"));
            questions.add(new Question("Fascinating", "매혹적인"));
            questions.add(new Question("Generous", "관대한"));
            questions.add(new Question("Imagination", "상상력"));
            questions.add(new Question("Journey", "여행"));
            questions.add(new Question("Knowledge", "지식"));
            questions.add(new Question("Mysterious", "신비로운"));
            questions.add(new Question("Respectful", "존경하는"));
            questions.add(new Question("Satisfying", "만족스러운"));
            questions.add(new Question("Surprising", "놀라운"));
            questions.add(new Question("Traditional", "전통적인"));
            questions.add(new Question("Unique", "독특한"));
            questions.add(new Question("Valuable", "귀중한"));
            questions.add(new Question("Wonderful", "멋진"));
            questions.add(new Question("Eager", "열망하는"));
            questions.add(new Question("Creative", "창의적인"));
            questions.add(new Question("Energetic", "활기찬"));
            questions.add(new Question("Friendly", "친근한"));
            questions.add(new Question("Grateful", "감사하는"));
            questions.add(new Question("Honest", "정직한"));
            questions.add(new Question("Joyful", "기쁜"));
            questions.add(new Question("Loyal", "충실한"));
            questions.add(new Question("Patient", "인내하는"));
            questions.add(new Question("Resourceful", "지혜로운"));
            questions.add(new Question("Sincere", "진실한"));
            questions.add(new Question("Thoughtful", "사려 깊은"));
            questions.add(new Question("Trustworthy", "신뢰할 수 있는"));
            questions.add(new Question("Ambitious", "야망 있는"));
            questions.add(new Question("Calm", "차분한"));
            questions.add(new Question("Confident", "자신감 있는"));
            questions.add(new Question("Delightful", "기쁜"));
            questions.add(new Question("Enthusiastic", "열정적인"));
            questions.add(new Question("Fascinating", "매혹적인"));
            questions.add(new Question("Genuine", "진정한"));
            questions.add(new Question("Humble", "겸손한"));
            questions.add(new Question("Impressive", "인상적인"));
            questions.add(new Question("Inspiring", "영감을 주는"));
            questions.add(new Question("Motivated", "동기 부여된"));
            questions.add(new Question("Optimistic", "낙관적인"));
            questions.add(new Question("Passionate", "열정적인"));
            questions.add(new Question("Reliable", "신뢰할 수 있는"));
            questions.add(new Question("Supportive", "지원하는"));
            questions.add(new Question("Thoughtful", "사려 깊은"));
            questions.add(new Question("Vibrant", "활기찬"));
        }
        if (difficulty.equals("hard")) {
            questions.add(new Question("Ambiguous", "모호한"));
            questions.add(new Question("Benevolent", "자비로운"));
            questions.add(new Question("Cacophony", "불협화음"));
            questions.add(new Question("Debilitate", "약화시키다"));
            questions.add(new Question("Eloquent", "웅변적인"));
            questions.add(new Question("Fortuitous", "우연의"));
            questions.add(new Question("Gregarious", "사교적인"));
            questions.add(new Question("Hapless", "불운한"));
            questions.add(new Question("Ineffable", "말로 표현할 수 없는"));
            questions.add(new Question("Juxtaposition", "병렬"));
            questions.add(new Question("Lethargic", "무기력한"));
            questions.add(new Question("Munificent", "아낌없이 주는"));
            questions.add(new Question("Nefarious", "사악한"));
            questions.add(new Question("Obfuscate", "혼란스럽게 하다"));
            questions.add(new Question("Pernicious", "해로운"));
            questions.add(new Question("Quintessential", "전형적인"));
            questions.add(new Question("Recalcitrant", "반항적인"));
            questions.add(new Question("Sagacious", "현명한"));
            questions.add(new Question("Tenacious", "고집 센"));
            questions.add(new Question("Ubiquitous", "어디에나 있는"));
            questions.add(new Question("Vehement", "격렬한"));
            questions.add(new Question("Wistful", "아련한 그리움"));
            questions.add(new Question("Xenophobia", "외국인 혐오"));
            questions.add(new Question("Yonder", "저쪽에"));
            questions.add(new Question("Zealous", "열성적인"));
            questions.add(new Question("Acrimonious", "신랄한"));
            questions.add(new Question("Brevity", "간결함"));
            questions.add(new Question("Candid", "솔직한"));
            questions.add(new Question("Dichotomy", "이분법"));
            questions.add(new Question("Emulate", "모방하다"));
            questions.add(new Question("Fractious", "다루기 힘든"));
            questions.add(new Question("Germane", "적절한"));
            questions.add(new Question("Hegemony", "패권"));
            questions.add(new Question("Irascible", "화를 잘 내는"));
            questions.add(new Question("Juvenile", "청소년의"));
            questions.add(new Question("Kaleidoscope", "만화경"));
            questions.add(new Question("Languid", "힘없는"));
            questions.add(new Question("Mundane", "일상적인"));
            questions.add(new Question("Nonchalant", "무관심한"));
            questions.add(new Question("Obsolete", "구식의"));
            questions.add(new Question("Paradox", "역설"));
            questions.add(new Question("Quixotic", "비현실적인"));
            questions.add(new Question("Rejuvenate", "다시 젊어지게 하다"));
            questions.add(new Question("Sycophant", "아부하는 사람"));
            questions.add(new Question("Trepidation", "두려움"));
            questions.add(new Question("Untenable", "방어할 수 없ㄷㄲ는"));
            questions.add(new Question("Vicarious", "대리의"));
            questions.add(new Question("Whimsical", "변덕스러운"));
            questions.add(new Question("Yoke", "결합하다"));
            questions.add(new Question("Zephyr", "미풍"));
        }
    }

    private void setNewQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getWord());
            userInputEditText.setText(""); // 이전 입력 초기화
            currentQuestionIndex++;
        } else {
            isGameActive = false;
            endGame(); // 모든 질문이 끝났을 때 게임 종료 처리
        }
    }

    private void checkAnswer() {
        if (!isGameActive) return;

        String userAnswer = userInputEditText.getText().toString().trim();
        Question currentQuestion = questions.get(currentQuestionIndex - 1);

        // 정답을 맞춘 경우
        if (userAnswer.equalsIgnoreCase(currentQuestion.getMeaning())) {
            score++;
            correctSound.start();
            scoreTextView.setText("점수: " + score);
        } else {
            incorrectSound.start();
            wrongAnswers.add(currentQuestion.getWord() + ": " + currentQuestion.getMeaning());
        }

        // 다음 질문으로 넘어가기
        setNewQuestion();
    }

    private void endGame() {
        Intent intent = new Intent(EnglishQuizActivity.this, GameOverActivity.class);
        intent.putExtra("score", score); // 점수 전달
        intent.putExtra("name",name);
        startActivity(intent);
        finish();
    }

    private void showWrongAnswers() {
        if (wrongAnswers.isEmpty()) {
            wrongAnswersTextView.setText("틀린 문제가 없습니다.");
        } else {
            StringBuilder wrongAnswersMessage = new StringBuilder("틀린 문제:\n");
            for (String answer : wrongAnswers) {
                wrongAnswersMessage.append(answer).append("\n");
            }
            wrongAnswersTextView.setText(wrongAnswersMessage.toString());
        }
        wrongAnswersTextView.setVisibility(View.VISIBLE);
    }
}
