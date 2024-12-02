package com.example.project4.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project4.MainActivity;
import com.example.project4.databinding.FragmentHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            String userId = currentUser.getUid();
            userRef = database.getReference("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        if (name != null) {
                            binding.textViewName.setText("이름: " + name);
                        } else {
                            binding.textViewName.setText("Name not found.");
                        }
                        if (email != null) {
                            binding.textViewEmail.setText("이메일: " + email);
                        } else {
                            binding.textViewEmail.setText("Email not found.");
                        }

                        setScoreText(snapshot, "scores/간단한 연산 게임", binding.textViewScore1, "간단한 연산 게임");
                        setScoreText(snapshot, "scores/받아쓰기 게임", binding.textViewScore2, "받아쓰기 게임");
                        setScoreText(snapshot, "scores/상식 퀴즈 게임", binding.textViewScore3, "상식 퀴즈 게임");
                        setScoreText(snapshot, "scores/영어 단어 게임", binding.textViewScore4, "영어 단어 게임");
                        setScoreText(snapshot, "scores/카드 뒤집기 게임", binding.textViewScore5, "카드 뒤집기 게임");
                        setScoreText(snapshot, "scores/카트 담기 게임", binding.textViewScore6, "카트 담기 게임");
                    } else {
                        Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            binding.textViewName.setText("No user found.");
            binding.textViewEmail.setText("No email found.");
        }

        // 로그아웃 버튼 설정
        binding.logoutButton.setOnClickListener(v -> {
            // Firebase 로그아웃
            auth.signOut();

            // Google 로그아웃
            GoogleSignIn.getClient(
                    requireContext(),
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut().addOnCompleteListener(task -> {
                // 로그아웃 후 MainActivity로 이동
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        });

        return root;

    }

    /**
     * Firebase에서 점수를 가져와 TextView에 설정
     * @param snapshot Firebase 데이터 스냅샷
     * @param path 점수 데이터 경로
     * @param textView 점수를 표시할 TextView
     * @param gameName 게임 이름
     */
    private void setScoreText(DataSnapshot snapshot, String path, View textView, String gameName) {
        Integer score = snapshot.child(path).getValue(Integer.class);
        if (score != null) {
            ((android.widget.TextView) textView).setText(gameName + ": " + score);
        } else {
            ((android.widget.TextView) textView).setText(gameName + ": 0");
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
