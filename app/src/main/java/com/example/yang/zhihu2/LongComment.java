package com.example.yang.zhihu2;

public class LongComment {
    String author;
    String id;
    String content;
    String avatar;
    String likes;
    String time;
    String reply_content;
    String reply_status;
    String reply_id;
    String reply_author;
    String reply_err_msg;
    String JsonLength;
    public LongComment(String author,String id,String content,String avatar,String likes,String time,String reply_content,String reply_status,String reply_id,String reply_author,String reply_err_msg,String JsonLength){
        this.author=author;
        this.id=id;
        this.content=content;
        this.avatar =avatar ;
        this.likes=likes;
        this.time=time;
        this.reply_content=reply_content;
        this.reply_status=reply_status;
        this.reply_id=reply_id;
        this.reply_author=reply_author;
        this.reply_err_msg=reply_err_msg;
        this.JsonLength=JsonLength;
    }
    public String getAuthor(){
        return author;
    }
    public String getId(){
        return id;
    }
    public String getContent(){
        return content;
    }
    public String getAvatar(){
        return avatar ;
    }
    public String getLikes(){
        return likes;
    }
    public String getTime(){
        return time;
    }
    public String getReply_content(){
        return reply_content;
    }
    public String getReply_status(){
        return reply_status;
    }
    public String getReply_id(){
        return reply_id;
    }
    public String getReply_author(){
        return reply_author;
    }
    public String getReply_err_msg(){
        return reply_err_msg;
    }
    public String getJsonLength(){
        return JsonLength;
    }
}

