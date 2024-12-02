package com.example.project4.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project4.R;
import com.example.project4.databinding.FragmentNotificationsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    private RecyclerView[] scoreRecyclerViews = new RecyclerView[6];
    private ScoreAdapter[] scoreAdapters = new ScoreAdapter[6];
    private final String[] scoreTypes = {
            "간단한 연산 게임",
            "받아쓰기 게임",
            "상식 퀴즈 게임",
            "영어 단어 게임",
            "카드 뒤집기 게임",
            "카트 담기 게임"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerViews and Adapters
        scoreRecyclerViews[0] = root.findViewById(R.id.score1RecyclerView);
        scoreRecyclerViews[1] = root.findViewById(R.id.score2RecyclerView);
        scoreRecyclerViews[2] = root.findViewById(R.id.score3RecyclerView);
        scoreRecyclerViews[3] = root.findViewById(R.id.score4RecyclerView);
        scoreRecyclerViews[4] = root.findViewById(R.id.score5RecyclerView);
        scoreRecyclerViews[5] = root.findViewById(R.id.score6RecyclerView);

        for (int i = 0; i < scoreRecyclerViews.length; i++) {
            scoreRecyclerViews[i].setLayoutManager(new LinearLayoutManager(getContext()));
            scoreAdapters[i] = new ScoreAdapter();
            scoreRecyclerViews[i].setAdapter(scoreAdapters[i]);

            // Load top scores for each score type
            loadTop10Scores(scoreTypes[i], scoreAdapters[i]);
        }

        return root;
    }

    private void loadTop10Scores(String scoreType, ScoreAdapter adapter) {
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("users");

        Query topScoresQuery = scoresRef.orderByChild("scores/" + scoreType).limitToLast(10);
        topScoresQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ScoreEntry> topScores = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userName = userSnapshot.child("name").getValue(String.class);
                    Integer score = userSnapshot.child("scores").child(scoreType).getValue(Integer.class);
                    if (userName != null && score != null) {
                        topScores.add(new ScoreEntry(userName, score));
                    }
                }

                // Sort in descending order
                Collections.sort(topScores, Comparator.comparingInt(ScoreEntry::getScore).reversed());

                // Update RecyclerView
                adapter.updateScores(topScores);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load top scores for " + scoreType, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ScoreEntry class
    public static class ScoreEntry {
        private String userName;
        private int score;

        public ScoreEntry(String userName, int score) {
            this.userName = userName;
            this.score = score;
        }

        public String getUserName() {
            return userName;
        }

        public int getScore() {
            return score;
        }
    }
}
