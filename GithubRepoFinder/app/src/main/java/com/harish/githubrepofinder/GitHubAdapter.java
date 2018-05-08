package com.harish.githubrepofinder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GitHubAdapter extends RecyclerView.Adapter<GitHubAdapter.GitHubViewHolder>{

    private Context context;
    private List<GitHubRepoInfo> repoList;

    public GitHubAdapter(Context context){
        this.context = context;
        repoList = new ArrayList<>();
    }

    @Override
    public GitHubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_row, parent, false);
        return new GitHubViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GitHubViewHolder holder, int position) {
        GitHubRepoInfo gitHubRepoInfo = repoList.get(position);
        holder.bind(gitHubRepoInfo);
    }

    @Override
    public int getItemCount() {
        return repoList.size();
    }

    public class GitHubViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView textViewRepoName, textViewStar, textViewOwner, textViewDescription;

        public GitHubViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewOwner = itemView.findViewById(R.id.textViewOwner);
            textViewRepoName = itemView.findViewById(R.id.textViewRepoName);
            textViewStar = itemView.findViewById(R.id.textViewStar);
        }

        public void bind(GitHubRepoInfo gitHubRepoInfo){
            textViewStar.setText("\u2605"+gitHubRepoInfo.getStars());
            textViewRepoName.setText(gitHubRepoInfo.getRepoName());
            textViewOwner.setText(gitHubRepoInfo.getOwner());
            textViewDescription.setText(gitHubRepoInfo.getDescription());
        }
    }

    public void reload(List<GitHubRepoInfo> list){
        repoList.clear();
        repoList.addAll(list);
        notifyDataSetChanged();
    }
}