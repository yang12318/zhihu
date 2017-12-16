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

public class ShortCommentAdapter extends RecyclerView.Adapter<ShortCommentAdapter.ViewHolder>{

    private List<ShortComment> mShortCommentList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View short_commentView;
        ImageView short_commentImage;
        TextView short_commentAuthor;
        TextView reply_short_commentAuthor;
        TextView short_commentTime;
        TextView short_commentLikes;
        TextView short_commentContent;
        TextView reply_Short_commentContent;

        public ViewHolder(View view) {
            super(view);
            short_commentView = view;
            short_commentImage = view.findViewById(R.id.avatar);
            short_commentTime = view.findViewById(R.id.time);
            short_commentAuthor=view.findViewById(R.id.author);
            short_commentContent=view.findViewById(R.id.content);
            short_commentLikes=view.findViewById(R.id.likes);
            reply_short_commentAuthor=view.findViewById(R.id.reply_author);
            reply_Short_commentContent=view.findViewById(R.id.reply_content);
        }
    }

    public ShortCommentAdapter(List<ShortComment> short_commentList) {
        mShortCommentList = short_commentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.short_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ShortComment short_comment = mShortCommentList.get(position);
        holder.short_commentAuthor.setText(short_comment.getAuthor());
        holder.reply_short_commentAuthor.setText(short_comment.getReply_author());
        holder.short_commentLikes.setText(short_comment.getLikes());
        holder.short_commentTime.setText(short_comment.getTime());
        holder.short_commentContent.setText(short_comment.getContent());
        holder.reply_Short_commentContent.setText(short_comment.getReply_content());
        Glide.with(mContext).load(short_comment.getAvatar()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.short_commentImage);
    }
    @Override
    public int getItemCount() {
        return mShortCommentList.size();
    }
}