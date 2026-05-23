package com.sporthub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sporthub.R;
import com.sporthub.data.model.SocialPost;

public class SocialAdapter extends ListAdapter<SocialPost, SocialAdapter.SocialViewHolder> {

    public SocialAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<SocialPost> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SocialPost>() {
                @Override
                public boolean areItemsTheSame(@NonNull SocialPost oldItem, @NonNull SocialPost newItem) {
                    return oldItem.getPostId() != null
                            && oldItem.getPostId().equals(newItem.getPostId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull SocialPost oldItem, @NonNull SocialPost newItem) {
                    return oldItem.getContent().equals(newItem.getContent());
                }
            };

    @NonNull
    @Override
    public SocialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new SocialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialViewHolder holder, int position) {
        SocialPost post = getItem(position);
        holder.bind(post);
    }

    static class SocialViewHolder extends RecyclerView.ViewHolder {
        private final TextView postContent;

        public SocialViewHolder(@NonNull View itemView) {
            super(itemView);
            postContent = itemView.findViewById(R.id.exercise_name);
        }

        public void bind(SocialPost post) {
            postContent.setText(post.getContent());
        }
    }
}
