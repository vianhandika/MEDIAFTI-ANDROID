package com.PBPProject.mediafti;

public class EventDAO {

    private String title,place,image,date;

    public EventDAO(){

    }

    public EventDAO(String title, String place, String image, String date) {
        this.title = title;
        this.place = place;
        this.image = image;
        this.date = date;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
