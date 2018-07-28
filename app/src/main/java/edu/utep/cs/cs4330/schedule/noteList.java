package edu.utep.cs.cs4330.schedule;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class noteList extends AppCompatActivity {
    ListView listView;
    List<Note> notes;
    NoteListDatabaseHelper helper;
    Button addNoteBtn;
    noteListAdapter adapter;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        helper = new NoteListDatabaseHelper(this);
        notes = new ArrayList<Note>();

        createNoteListFromDB();

        listView = findViewById(R.id.listView);
        adapter = new noteListAdapter(this, R.layout.list_note_template, notes, helper);
        listView.setAdapter(adapter);

        addNoteBtn = findViewById(R.id.addBtn);
        addNoteBtn.setOnClickListener(this::displayAddNoteDialog);

    }

    protected void displayAddNoteDialog(View view){
        builder = new AlertDialog.Builder(this);

        View dView = getLayoutInflater().inflate(R.layout.dialog_add_note, null);
        EditText title = (EditText) dView.findViewById(R.id.title);
        EditText body = (EditText) dView.findViewById(R.id.body);
        Button addBtn = (Button) dView.findViewById(R.id.add);
        Button cancelBtn = (Button) dView.findViewById(R.id.cancel);
        builder.setView(dView);
        AlertDialog dialog = builder.create();
        dialog.show();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleTxt = title.getText().toString();
                String bodyTxt = body.getText().toString();

                if(titleTxt.length() < 1 || bodyTxt.length() < 1){
                    Toast.makeText(getApplicationContext(),"One or more fields are too short or empty", Toast.LENGTH_SHORT);
                }
                else {
                    addNote(titleTxt, bodyTxt);
                }
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void addNote(String title, String body){
        new Thread(() -> {
            // Do in background
            // We should parse in here
            Note note = new Note(title, body, "Some Category", "Today");

            long rowId = helper.addItem(note);

            runOnUiThread(() -> { // UI
                if (rowId != -1) {
                    note.setId((int)rowId);
                    notes.add(note);
                    adapter.swapItems(notes);
                    Log.e("Success: ", "DB Add Operation Succeeded");
                } else {
                    Log.e("Error: ", "DB Add Operation failed");
                }
            });
        }).start();
    }

    protected void createNoteListFromDB(){
        new Thread(() -> {
            // Do in background
            boolean result = helper.query(notes);

            runOnUiThread(() -> { // UI
                if (result == true) {
                    adapter.swapItems(notes);
                    Log.e("Success: ", "DB Query Operation Succeeded");
                } else {
                    Log.e("Error: ", "DB Query Operation failed");
                }
            });
        }).start();
    }

    @Override
    public void onResume(){
        super.onResume();
//        createNoteListFromDB();
        adapter.swapItems(notes);
    }

}
