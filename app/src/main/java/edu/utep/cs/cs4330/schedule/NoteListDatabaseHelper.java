package edu.utep.cs.cs4330.schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class NoteListDatabaseHelper  extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "NoteListDB";
    private static final String NOTE_TABLE = "notes";

    private static final String KEY_ID = "_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_BODY = "body";
    private static final String KEY_DATE = "date";
    private static final String KEY_CATEGORY= "category";


    public NoteListDatabaseHelper(Context ctx) {
        super(ctx,DB_NAME, null, DB_VERSION );
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
        db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);
        onCreate(db);
    }

    public long addItem(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_BODY, note.getBody());
        values.put(KEY_CATEGORY, note.getCategory());
        values.put(KEY_DATE, note.getDate());

        long success = db.insert(NOTE_TABLE, null, values);
        db.close();
        return success;
    }

    public boolean query(List notes){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NOTE_TABLE,
                new String[] { KEY_ID, KEY_TITLE, KEY_BODY, KEY_DATE, KEY_CATEGORY },
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do { // Construct item list here
                String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
                String body = cursor.getString(cursor.getColumnIndex(KEY_BODY));
                String date = cursor.getString(cursor.getColumnIndex(KEY_DATE));
                String category = cursor.getString(cursor.getColumnIndex(KEY_CATEGORY));
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));

                Note note = new Note(title, body, category, date, id);

                notes.add(note);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return true;
    }

    public int deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int success = db.delete(NOTE_TABLE,
                KEY_ID + " = ?",
                new String[] { Integer.toString(id) } );
        db.close();
        return success;
    }

    public int updateItem(int id, Note note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_BODY, note.getBody());
        values.put(KEY_CATEGORY, note.getCategory());
        values.put(KEY_DATE, note.getDate());

        int success = db.update(NOTE_TABLE,
                values,
                KEY_ID + " = ?",
                new String[] { Integer.toString(id) } );
        db.close();
        return success;
    }
}
