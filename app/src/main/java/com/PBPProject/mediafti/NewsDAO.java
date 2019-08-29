package com.PBPProject.mediafti;

public class NewsDAO {
    private String title,desc,image,from;

    public NewsDAO(){

    }

    public NewsDAO(String title, String desc, String image, String from) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.from = from;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
