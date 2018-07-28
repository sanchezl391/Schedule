package edu.utep.cs.cs4330.schedule;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class noteList extends AppCompatActivity {
    ListView listView;
    List<Note> notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);



//         * Adds listener fot floating action button
//         *
//         *
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
        }
        });


        Note newNote = new Note("Title", "Body", "category", "Date");
        notes = new ArrayList<Note>();
        notes.add(newNote);

        listView = findViewById(R.id.listView);
        noteListAdapter adapter = new noteListAdapter(this, R.layout.list_note_template, notes);
        listView.setAdapter(adapter);

    }
}
