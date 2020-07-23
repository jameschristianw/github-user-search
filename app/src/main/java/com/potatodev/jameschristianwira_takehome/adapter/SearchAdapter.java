package com.potatodev.jameschristianwira_takehome.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.potatodev.jameschristianwira_takehome.R;
import com.potatodev.jameschristianwira_takehome.models.User;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    List<User> users;
    Context context;

    public SearchAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    public SearchAdapter() {
    }

    public void updateResult(List<User> moreUsers) {
        users.addAll(moreUsers);
    }

    public void clearList() {
        if (users != null) {
            users.clear();
        }
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        User user = users.get(position);

        holder.tvUsername.setText(user.getLogin());
        Glide.with(context).load(user.getAvatarUrl()).into(holder.ivUserPhoto);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername;
        ImageView ivUserPhoto;
        CardView cvResult;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tvResultName);
            ivUserPhoto = itemView.findViewById(R.id.ivUserPhoto);
            cvResult = itemView.findViewById(R.id.cvResult);
        }
    }
}
