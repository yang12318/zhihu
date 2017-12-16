package com.example.yang.zhihu2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{

    private List<News> mNewsList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View newsView;
        ImageView newsImage;
        TextView newsTitle;
        TextView newsDate;

        public ViewHolder(View view) {
            super(view);
            newsView = view;
            newsImage = view.findViewById(R.id.news_image);
            newsTitle = view.findViewById(R.id.news_title);
            newsDate=view.findViewById(R.id.news_date);
        }
    }

    public NewsAdapter(List<News> newsList) {
        mNewsList = newsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final News news = mNewsList.get(position);
        holder.newsTitle.setText(news.getTitle());
        holder.newsDate.setText(news.getDisplay_date());
        Glide.with(mContext).load(news.getImages()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.newsImage);
    }
    @Override
    public int getItemCount() {
        return mNewsList.size();
    }
}


