package edu.utep.cs.cs4330.schedule;

import java.util.Calendar;

public class Note {
    private String title;
    private String body;
    private String category;
    private int id;
    private String time;

    /**
     * Initializes a Note object containing note attributes
     * @param title the title of a note
     * @param body the body of a note
     * @param category the category a note belongs to
     * @param date the date for the notification of a note
     */
    public Note (String title, String body, String category, String date){
        this.title = title;
        this.body = body;
        this.category = category;
        this.time = "";
    }

    /**
     * Sets the note's time
     * @param time contains year, month, day, hour, minute in a string format
     */
    public void setTime(String time){
        this.time = time;
    }

    /**
     * retrieves notification time id of a note
     * @return the notification time of a note
     */
    public String getTime(){
        return time;
    }

    /**
     * retrieves the title of a note
     * @return the title of a note
     */
    public String getTitle() {
        return title;
    }

    /**
     * retrieves the category of a note
     * @return the category of a note
     */
    public String getCategory() {
        return category;
    }

    /**
     * retrieves the body of a note
     * @return the id body a note
     */
    public String getBody() {
        return body;
    }

    /**
     * retrieves the id of a note
     * @return the id of a note
     */
    public int getId(){
        return id;
    }

    /**
     * Sets the note's Id
     * @param id the id of the row in the database for a note
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Sets the note's title
     * @param title the title of a note
     */
    public void setTitle(String title){this.title = title;}

    /**
     * Sets the note's body
     * @param body the body of a note
     */
    public void setBody(String body){this.body = body;}

    /**
     * Sets the note's category
     * @param category the category a note belongs to
     */
    public void setCategory(String category){
        this.category = category;
    }


}
