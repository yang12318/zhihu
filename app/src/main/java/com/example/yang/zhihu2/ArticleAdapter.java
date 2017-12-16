package com.example.yang.zhihu2;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>{

    private List<Article> mArticleList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View articleView;
        CardView cardView;
        ImageView articleImage;
        TextView articleName;
        TextView articleDescription;

        public ViewHolder(View view) {
            super(view);
            articleView=view;
            cardView = (CardView) view;
            articleImage = view.findViewById(R.id.article_image);
            articleName = view.findViewById(R.id.article_name);
            articleDescription=view.findViewById(R.id.article_description);
        }
    }


    public ArticleAdapter(List<Article> articleList) {
        mArticleList = articleList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.article_item, parent, false);
        final ViewHolder holder=new ViewHolder(view);
        holder.articleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                final Article article = mArticleList.get(position);
                Intent intent=new Intent(mContext,ArticleMain.class);
                intent.putExtra("articleId_intent",article.getId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Article article = mArticleList.get(position);
        holder.articleName.setText(article.getName());
        holder.articleDescription.setText(article.getDescription());
        Glide.with(mContext).load(article.getThumbnail()).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(holder.articleImage);

    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

}
