package com.example.project4;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameOverActivity extends AppCompatActivity {

    private TextView scoreTextView, nameTextView;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        scoreTextView = findViewById(R.id.scoreTextView);
        nameTextView = findViewById(R.id.nameTextView);

        // Intent로부터 데이터 받기
        String gameName = getIntent().getStringExtra("name");
        int score = getIntent().getIntExtra("score", 0);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // 현재 사용자의 UID 가져오기
            String userId = currentUser.getUid();
            userRef = database.getReference("users").child(userId);

            // 사용자 이름 가져오기
            String userName = currentUser.getDisplayName();
            if (userName == null || userName.isEmpty()) {
                userName = "Unknown User"; // 사용자 이름이 없는 경우 기본값
            }

            // TextView에 표시
            scoreTextView.setText("Your Score: " + score);
            nameTextView.setText("Game Name: " + gameName);

            // Firebase에 점수 저장 (기존 점수와 비교)
            checkAndSaveScore(gameName, score);

        } else {
            Toast.makeText(this, "No user logged in. Cannot save score.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 기존 점수와 비교하여 새로운 점수가 높으면 저장
     * @param gameName 게임 이름
     * @param score    점수
     */
    private void checkAndSaveScore(String gameName, int score) {
        userRef.child("scores").child(gameName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    int existingScore = snapshot.getValue(Integer.class); // 기존 점수 가져오기
                    if (score > existingScore) {
                        saveScoreToFirebase(gameName, score);
                    } else {
                        Toast.makeText(GameOverActivity.this, "Score not high enough to save.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 점수가 처음 저장되는 경우
                    saveScoreToFirebase(gameName, score);
                }
            } else {
                Toast.makeText(GameOverActivity.this, "Failed to retrieve existing score.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Firebase에 점수 저장 메서드
     * @param gameName 게임 이름
     * @param score    점수
     */
    private void saveScoreToFirebase(String gameName, int score) {
        userRef.child("scores").child(gameName).setValue(score).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(GameOverActivity.this, "Score saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GameOverActivity.this, "Failed to save score.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void returnToMainMenu(android.view.View view) {
        finish(); // 현재 Activity 종료
    }
}
