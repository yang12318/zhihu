package com.example.yang.zhihu2;

public class News {
    String title;
    String id;
    String display_date;
    String images;
    public News(String title,String id,String display_date,String images){
        this.title=title;
        this.id=id;
        this.display_date=display_date;
        this.images=images;
    }
    public String getTitle(){
        return title;
    }
    public String getId(){
        return id;
    }
    public String getDisplay_date(){
        return display_date;
    }
    public String getImages(){
        return images;
    }
}
