package com.example.project4;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    TextView userName, userEmail, highestScoreTextView, top10ScoresTextView;
    EditText numberInput;
    Button saveButton, showTop10Button;
    Spinner scoreTypeSpinner;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        userName = findViewById(R.id.userNameTextView);
        userEmail = findViewById(R.id.userEmailTextView);
        highestScoreTextView = findViewById(R.id.highestScoreTextView);
        top10ScoresTextView = findViewById(R.id.top10ScoresTextView);
        numberInput = findViewById(R.id.numberInput);
        saveButton = findViewById(R.id.saveButton);
        showTop10Button = findViewById(R.id.showTop10Button);
        scoreTypeSpinner = findViewById(R.id.scoreTypeSpinner);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        // Score types array
        String[] scoreTypes = {"score1", "score2", "score3", "score4", "score5", "score6"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, scoreTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scoreTypeSpinner.setAdapter(adapter);

        if (currentUser != null) {
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            userName.setText(name);
            userEmail.setText(email);

            userRef = database.getReference().child("users").child(currentUser.getUid());

            // 저장 버튼 클릭 시의 동작
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String numberStr = numberInput.getText().toString();
                    String selectedScoreType = scoreTypeSpinner.getSelectedItem().toString();

                    if (!numberStr.isEmpty()) {
                        int newValue = Integer.parseInt(numberStr);
                        userRef.child("scores").child(selectedScoreType).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int currentValue = 0;
                                if (snapshot.exists()) {
                                    currentValue = snapshot.getValue(Integer.class);
                                }

                                // 새로운 값이 기존 값보다 큰 경우 업데이트
                                if (newValue > currentValue) {
                                    userRef.child("scores").child(selectedScoreType).setValue(newValue).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            highestScoreTextView.setText("Highest Score (" + selectedScoreType + "): " + newValue);
                                            Toast.makeText(SecondActivity.this, "New highest score: " + newValue, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SecondActivity.this, "Failed to update score", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(SecondActivity.this, "Score not updated. Current highest score: " + currentValue, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(SecondActivity.this, "Failed to read current score", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(SecondActivity.this, "Please enter a number", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Top 10 버튼 클릭 시의 동작
            showTop10Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedScoreType = scoreTypeSpinner.getSelectedItem().toString();
                    DatabaseReference scoresRef = database.getReference().child("users");

                    Query topScoresQuery = scoresRef.orderByChild("scores/" + selectedScoreType).limitToLast(10);
                    topScoresQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<String> topScores = new ArrayList<>();
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                String userName = userSnapshot.child("name").getValue(String.class);
                                Integer score = userSnapshot.child("scores").child(selectedScoreType).getValue(Integer.class);
                                if (userName != null && score != null) {
                                    topScores.add(userName + ": " + score);
                                }
                            }

                            // 내림차순 정렬 (Firebase 쿼리는 오름차순이므로)
                            Collections.reverse(topScores);

                            // Top 10 결과 출력
                            StringBuilder top10Text = new StringBuilder("Top 10 Scores (" + selectedScoreType + "):\n");
                            for (String entry : topScores) {
                                top10Text.append(entry).append("\n");
                            }
                            top10ScoresTextView.setText(top10Text.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(SecondActivity.this, "Failed to load top scores", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            userName.setText("No user found");
            userEmail.setText("No email found");
        }
    }
}