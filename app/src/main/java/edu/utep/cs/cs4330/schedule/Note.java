package edu.utep.cs.cs4330.schedule;

public class Note {
    private String title;
    private String body;
    private String category;
    private String date;
    private int id;

    public Note (String title, String body, String category, String date, int id){
        this.title = title;
        this.body = body;
        this.category = category;
        this.date = date;
        this.id = id;
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

    public String getDate() {
        return date;
    }

//    public void setId(int id){
//        this.id = id;
//    }
}
