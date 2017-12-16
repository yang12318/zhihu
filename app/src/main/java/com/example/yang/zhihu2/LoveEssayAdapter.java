package com.example.yang.zhihu2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LoveEssayAdapter extends RecyclerView.Adapter<LoveEssayAdapter.ViewHolder>{

    private List<LoveEssay> mLoveEssayList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View love_essayView;
        ImageView love_essayImage;
        TextView love_essayTitle;

        public ViewHolder(View view) {
            super(view);
            love_essayView = view;
            love_essayImage = view.findViewById(R.id.love_essay_image);
            love_essayTitle = view.findViewById(R.id.love_essay_title);
        }
    }

    public LoveEssayAdapter(List<LoveEssay> love_essayList) {
        mLoveEssayList = love_essayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.loveessay_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LoveEssay love_essay = mLoveEssayList.get(position);
        holder.love_essayTitle.setText(love_essay.getTitle());
        Glide.with(mContext).load(love_essay.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.love_essayImage);
    }
    @Override
    public int getItemCount() {
        return mLoveEssayList.size();
    }
}
