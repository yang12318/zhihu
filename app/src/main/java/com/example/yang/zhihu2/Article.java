package com.example.yang.zhihu2;

public class Article {

    private String name;
    String id;
    String description;
    String thumbnail;
    private int imageId;

    public Article(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }
    public Article(String name,String id,String description,String thumbnail){
        this.name=name;
        this.id=id;
        this.description=description;
        this.thumbnail=thumbnail;
    }

    public String getName() {
        return name;
    }
    public String getId(){
        return id;
    }
    public String getDescription(){
        return description;
    }
    public String getThumbnail(){
        return thumbnail;
    }
    public int getImageId() {
        return imageId;
    }

}