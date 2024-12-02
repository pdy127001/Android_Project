package com.example.project4.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.project4.CardActivity;
import com.example.project4.CartActivity;
import com.example.project4.EnglishQuizActivity;
import com.example.project4.MathQuizActivity;
import com.example.project4.QuizActivity;
import com.example.project4.TTSActivity;
import com.example.project4.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        // View Binding 초기화
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 버튼 클릭 이벤트 추가
        binding.Card.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardActivity.class);
            startActivity(intent);
        });
        binding.Quiz.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            startActivity(intent);
        });
        binding.EnglishQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EnglishQuizActivity.class);
            startActivity(intent);
        });
        binding.Cart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CartActivity.class);
            startActivity(intent);
        });
        binding.TTS.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TTSActivity.class);
            startActivity(intent);
        });
        binding.MathQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MathQuizActivity.class);
            startActivity(intent);
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
