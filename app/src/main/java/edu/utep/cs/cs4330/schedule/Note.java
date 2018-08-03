package edu.utep.cs.cs4330.schedule;

import java.util.Calendar;

public class Note {
    private String title;
    private String body;
    private String category;
    private int id;
    private String time;

    public Note (String title, String body, String category, String date){
        this.title = title;
        this.body = body;
        this.category = category;
        this.time = "";
//        this.id = id;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getBody() {
        return body;
    }


    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setTitle(String title){this.title = title;}

    public void setBody(String body){this.body = body;}

    public void setCategory(String category){
        this.category = category;
    }


}
