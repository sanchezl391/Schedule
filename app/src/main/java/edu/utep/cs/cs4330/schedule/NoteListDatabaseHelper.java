package edu.utep.cs.cs4330.schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class NoteListDatabaseHelper  extends SQLiteOpenHelper {
    private static int DB_VERSION;
    private static final String DB_NAME = "NoteListDB";
    private static final String NOTE_TABLE = "notes";
    private static final String CATEGORY_TABLE = "categories";

    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_DATE = "date";
    private static final String KEY_CATEGORY = "category";

    /**
     * Initialzes the DB helper object
     * @param ctx the application's context
     * @param version the DB's current version
     */
    public NoteListDatabaseHelper(Context ctx, int version) {
        super(ctx,DB_NAME, null, version );
    }

    /**
     * Creates the tables when the DB is created
     * @param db the object that allows DB operations
     */
    public void onCreate(SQLiteDatabase db){
        String sql = "CREATE TABLE " + NOTE_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " TEXT, "
                + KEY_BODY + " TEXT, "
                + KEY_CATEGORY + " TEXT, "
                + KEY_DATE + " TEXT "
                + ")";
        db.execSQL(sql);
    }

    /**
     * Called whenever there is change in database version
     * @param db the object that allows DB operations
     * @param oldVersion the DB's old version number
     * @param newVersion the DB's new version number
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        String sql = "CREATE TABLE " + CATEGORY_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CATEGORY + " TEXT "
                + ")";

        db.execSQL(sql);

    }

    /**
     * Adds a category to the DB
     * @param category category to be added to the DB
     * @return the row number to which the category was added
     */
    public long addCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_CATEGORY, category);

        long rowId = db.insert(CATEGORY_TABLE, null, values);
        db.close();
        return rowId;
    }

    /**
     * Adds a note to the DB
     * @param note the note that contains information that will be added to the DB
     * @return the row to which the note was added
     */
    public long addItem(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_BODY, note.getBody());
        values.put(KEY_CATEGORY, note.getCategory());
        values.put(KEY_DATE, note.getTime());

        long rowId = db.insert(NOTE_TABLE, null, values);
        db.close();
        return rowId;
    }

    /**
     * Queries the database and creates a list of notes and categories
     * @param notes the list of notes to which DB data will be saved onto
     * @param categories the list of categories to which DB data will be saved onto
     * @return true when all operations have been completed
     */
    public boolean query(List notes, List categories){
        notes.clear();
        categories.clear();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor noteCursor = db.query(NOTE_TABLE,
                new String[] { KEY_ID, KEY_TITLE, KEY_BODY, KEY_DATE, KEY_CATEGORY },
                null, null, null, null, null);

        Cursor categoryCursor = db.query(CATEGORY_TABLE,
                new String[] { KEY_ID, KEY_CATEGORY },
                null, null, null, null, null);

        createNoteList(noteCursor, notes);
        createCategoryList(categoryCursor, categories);

        db.close();
        return true;
    }

    /**
     * Helper method that creates the list of notes from the DB
     * @param cursor helps iterate through DB
     * @param notes list of notes that will contain the DB list of notes
     */
    public void createNoteList(Cursor cursor, List notes){
        if (cursor.moveToFirst()) {
            do { // Construct item list here
                String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                String body = cursor.getString(cursor.getColumnIndex(KEY_BODY));
                String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                String category = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY));
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));

                Note note = new Note(title, body, category, date);
                note.setTime(date);
                note.setId(id);

                notes.add(note);

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    /**
     * Helper method that creates the list of categories from the DB
     * @param cursor helps iterate through DB
     * @param categories list of categories that will contain the DB list of categories
     */
    public void createCategoryList(Cursor cursor, List categories){
        if (cursor.moveToFirst()) {
            do { // Construct item list here
                String category = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY));

                categories.add(category);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    /**
     * Deletes a category from the database
     * @param category the category to be deleted from the DB
     * @return the row from which the category was deleted
     */
    public int deleteCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        int success = db.delete(CATEGORY_TABLE,
                KEY_CATEGORY + " = ?",
                new String[] { category } );
        db.close();
        return success;
    }

    /**
     * delets a note from the DB
     * @param id the row where the note is stored
     * @return the row from which the note was deleted
     */
    public int deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int success = db.delete(NOTE_TABLE,
                KEY_ID + " = ?",
                new String[] { Integer.toString(id) } );
        db.close();
        return success;
    }

    /**
     * Updates the note data on the DB
     * @param note the note containing the new data
     * @return the amount of rows that were updated
     */
    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_BODY, note.getBody());
        values.put(KEY_CATEGORY, note.getCategory());
        values.put(KEY_DATE, note.getTime());

        int success = db.update(NOTE_TABLE,
                values,
                KEY_ID + " = ?",
                new String[] { Integer.toString(note.getId()) } );
        db.close();
        return success;
    }

    /**
     * updates a category name
     * @param category the name of the current category
     * @param newCategoryName the name of the new category
     * @return
     */
    public int updateCategory(String category, String newCategoryName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_CATEGORY, newCategoryName);

        int success = db.update(CATEGORY_TABLE,
                values,
                KEY_CATEGORY + " = ?",
                new String[] { category } );
        db.close();
        return success;
    }

}
