package com.example.yang.zhihu2;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LoveNewsAdapter extends RecyclerView.Adapter<LoveNewsAdapter.ViewHolder>{

    private List<LoveNews> mLoveNewsList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View love_newsView;
        CardView cardView;
        ImageView love_newsImage;
        TextView love_newsName;
        TextView love_newsDescription;

        public ViewHolder(View view) {
            super(view);
            love_newsView=view;
            cardView = (CardView) view;
            love_newsImage = view.findViewById(R.id.love_news_image);
            love_newsName = view.findViewById(R.id.love_news_name);
            love_newsDescription=view.findViewById(R.id.love_news_description);
        }
    }

    public LoveNewsAdapter(List<LoveNews>love_newsList) {
        mLoveNewsList = love_newsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.lovenews_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LoveNews love_news = mLoveNewsList.get(position);
        holder.love_newsName.setText(love_news.getName());
        holder.love_newsDescription.setText(love_news.getDescription());
        Glide.with(mContext).load(love_news.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.love_newsImage);
    }

    @Override
    public int getItemCount() {
        return mLoveNewsList.size();
    }

}