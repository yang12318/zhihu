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

public class LongCommentAdapter extends RecyclerView.Adapter<LongCommentAdapter.ViewHolder>{

    private List<LongComment> mLongCommentList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View long_commentView;
        ImageView long_commentImage;
        TextView long_commentAuthor;
        TextView reply_long_commentAuthor;
        TextView long_commentTime;
        TextView long_commentLikes;
        TextView long_commentContent;
        TextView reply_Long_commentContent;

        public ViewHolder(View view) {
            super(view);
            long_commentView = view;
            long_commentImage = view.findViewById(R.id.avatar);
            long_commentTime = view.findViewById(R.id.time);
            long_commentAuthor=view.findViewById(R.id.author);
            long_commentContent=view.findViewById(R.id.content);
            long_commentLikes=view.findViewById(R.id.likes);
            reply_long_commentAuthor=view.findViewById(R.id.reply_author);
            reply_Long_commentContent=view.findViewById(R.id.reply_content);
        }
    }

    public LongCommentAdapter(List<LongComment> long_commentList) {
        mLongCommentList = long_commentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.long_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final LongComment long_comment = mLongCommentList.get(position);
        holder.long_commentAuthor.setText(long_comment.getAuthor());
        holder.reply_long_commentAuthor.setText(long_comment.getReply_author());
        holder.long_commentLikes.setText(long_comment.getLikes());
        holder.long_commentTime.setText(long_comment.getTime());
        holder.long_commentContent.setText(long_comment.getContent());
        holder.reply_Long_commentContent.setText(long_comment.getReply_content());
        Glide.with(mContext).load(long_comment.getAvatar()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.long_commentImage);
    }
    @Override
    public int getItemCount() {
        return mLongCommentList.size();
    }
}