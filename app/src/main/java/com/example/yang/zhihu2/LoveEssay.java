package com.example.yang.zhihu2;

public class LoveEssay {
    String username;
    String title;
    String essay_id;
    String thumbnail;
    String url;
    LoveEssay(String username,String title, String essay_id, String thumbnail, String url){
        this.username=username;
        this.title=title;
        this.essay_id=essay_id;
        this.thumbnail=thumbnail;
        this.url=url;
    }
    public String getUsername(){
        return username;
    }
    public String getTitle(){
        return title;
    }
    public String getEssay_id(){
        return essay_id;
    }
    public String getThumbnail(){
        return thumbnail;
    }
    public String getUrl(){
        return url;
    }
}
