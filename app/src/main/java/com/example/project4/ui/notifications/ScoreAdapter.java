package com.example.project4.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project4.R;

import java.util.ArrayList;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private final List<NotificationsFragment.ScoreEntry> scores = new ArrayList<>();

    public void updateScores(List<NotificationsFragment.ScoreEntry> newScores) {
        scores.clear();
        scores.addAll(newScores);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        NotificationsFragment.ScoreEntry scoreEntry = scores.get(position);
        holder.bind(scoreEntry, position + 1);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    static class ScoreViewHolder extends RecyclerView.ViewHolder {

        private final TextView rankTextView;
        private final TextView userNameTextView;
        private final TextView scoreTextView;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }

        public void bind(NotificationsFragment.ScoreEntry scoreEntry, int rank) {
            rankTextView.setText(String.valueOf(rank));
            userNameTextView.setText(scoreEntry.getUserName());
            scoreTextView.setText(String.valueOf(scoreEntry.getScore()));
        }
    }
}
