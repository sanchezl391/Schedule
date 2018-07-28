package edu.utep.cs.cs4330.schedule;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class noteList extends AppCompatActivity {
    ListView listView;
    List<Note> notes;
    List<String> categories;
    NoteListDatabaseHelper helper;
    Button addNoteBtn;
    noteListAdapter adapter;
    AlertDialog.Builder builder;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        helper = new NoteListDatabaseHelper(this, 2);
        notes = new ArrayList<Note>();
        categories = new ArrayList<String>();

        createListsFromDB();
//        setupDrawer();


        listView = findViewById(R.id.listView);
        adapter = new noteListAdapter(this, R.layout.list_note_template, notes, helper);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Item Selected",
                        Toast.LENGTH_SHORT).show();
            }
        });


        addNoteBtn = findViewById(R.id.addBtn);
        addNoteBtn.setOnClickListener(this::displayAddNoteDialog);

    }

    public void setupDrawer(){
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

//        mDrawerLayout.addDrawerListener(
//                new DrawerLayout.DrawerListener() {
//                    @Override
//                    public void onDrawerSlide(View drawerView, float slideOffset) {
//                        // Respond when the drawer's position changes
//                    }
//
//                    @Override
//                    public void onDrawerOpened(View drawerView) {
//                        // Respond when the drawer is opened
//                    }
//
//                    @Override
//                    public void onDrawerClosed(View drawerView) {
//                        // Respond when the drawer is closed
//                    }
//
//                    @Override
//                    public void onDrawerStateChanged(int newState) {
//                        // Respond when the drawer motion state changes
//                    }
//                }
//        );


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            parseNote(title, body);
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

    public void parseNote(String title, String body){


        String[] categoryKeywords = {
                ""
        };

    }

    protected void createListsFromDB(){
        new Thread(() -> {
            // Do in background
            boolean result = helper.query(notes, categories);

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
        adapter.swapItems(notes);
    }

}
