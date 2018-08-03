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


    public NoteListDatabaseHelper(Context ctx, int version) {
        super(ctx,DB_NAME, null, version );
    }

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

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        String sql = "CREATE TABLE " + CATEGORY_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CATEGORY + " TEXT "
                + ")";

        db.execSQL(sql);

    }

    public long addCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_CATEGORY, category);

        long rowId = db.insert(CATEGORY_TABLE, null, values);
        db.close();
        return rowId;
    }

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

    public void createCategoryList(Cursor cursor, List categories){
        if (cursor.moveToFirst()) {
            do { // Construct item list here
                String category = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY));

                categories.add(category);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }


    public int deleteCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        int success = db.delete(CATEGORY_TABLE,
                KEY_CATEGORY + " = ?",
                new String[] { category } );
        db.close();
        return success;
    }

    public int deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int success = db.delete(NOTE_TABLE,
                KEY_ID + " = ?",
                new String[] { Integer.toString(id) } );
        db.close();
        return success;
    }

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
