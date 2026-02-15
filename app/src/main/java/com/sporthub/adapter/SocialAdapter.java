package com.sporthub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sporthub.R;
import com.sporthub.data.model.SocialPost;

import java.util.ArrayList;
import java.util.List;

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.SocialViewHolder> {
    private List<SocialPost> posts = new ArrayList<>();
    
    public void setPosts(List<SocialPost> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public SocialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_item, parent, false);
        return new SocialViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SocialViewHolder holder, int position) {
        SocialPost post = posts.get(position);
        holder.bind(post);
    }
    
    @Override
    public int getItemCount() {
        return posts.size();
    }
    
    static class SocialViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        
        public SocialViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
        }
        
        public void bind(SocialPost post) {
            productName.setText(post.getContent());
        }
    }
}
