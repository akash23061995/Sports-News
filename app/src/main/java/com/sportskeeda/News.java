package com.sportskeeda;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class News extends RealmObject {
    /*
    * The News is an object which is shown in the list format
    * The required attributes are associated with each news and hence private modifier is used.
    * Realm database internally stores the objects to avoid redundant network calls and hence also enhances UI experience for the end user.
    *
    * */

    private String title;
    private String type;  ///// Either article, slideshow or video.
    private String permalink;
    private String author;
    private String img;

    /*
    * Creating getters and setters.
    * */

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }




    public String getPermalink(){
        return permalink;
    }

    public void setPermalink(String permalink){
        this.permalink = permalink;
    }



    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }



    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    /*
    * Setting properties of attributes in Realm database.
    *
    * */

    public final static String PROPERTY_TITLE="news_title";
    public final static String PROPERTY_AUTHOR="news_author";
    public final static String PROPERTY_IMAGE="news_image";
    public final static String PROPERTY_TYPE = "news_type";
    public final static String PROPERTY_PERMALINK = "news_permalink";


    @PrimaryKey
    @Required
    public String news_title;
    public String news_author;
    public String news_type;
    public  String news_image;
    public  String news_permalink;


}
