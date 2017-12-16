package com.example.yang.zhihu2;

public class Hot {
    String title;
    String essay_id;
    String thumbnail;
    String url;
    public Hot(String title,String essay_id,String thumbnail,String url){
        this.title=title;
        this.essay_id=essay_id;
        this.thumbnail=thumbnail;
        this.url=url;
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
