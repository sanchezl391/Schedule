/**
 * Author: Luis Sanchez
 */

package edu.utep.cs.cs4330.schedule;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class noteList extends AppCompatActivity {
    ListView listView;
    List<Note> notes;
    List<String> categories;
    NoteListDatabaseHelper helper;
    noteListAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private boolean atLeastOneCategoryPresent = false;
    private boolean categoryBolded = false;
    private int drawerItemAmount = 0;
    private String currentCategorySelected = "All";
    private Parser parser = new Parser();
    private CategoryManager categoryManager;
    private NoteManager noteManager;
    private FloatingActionButton addNoteBtn;

    /**
     * setups up the layout for the activity and setups listeners
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        helper = new NoteListDatabaseHelper(this, 3);

        notes = new ArrayList<Note>();
        categories = new ArrayList<String>();

        createListsFromDB();
        createListFromList(notes); // this gets executed before db items are queried

        setupDrawer();
        categoryManager = new CategoryManager(helper, categories, adapter, this);
        noteManager = new NoteManager(helper, categories, adapter, this, notes, currentCategorySelected, atLeastOneCategoryPresent, categoryBolded);

        addNoteBtn = findViewById(R.id.addNoteBtn);
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteManager.displayAddNoteDialog(view);
            }
        });

    }

    /**
     * Inflates the list view with the provided list of notes
     * @param noteList list of notes that will be used to inflate the list
     */
    public void createListFromList(List<Note> noteList){
        listView = findViewById(R.id.listView);
        adapter = new noteListAdapter(this, R.layout.list_note_template, noteList, categories, helper, atLeastOneCategoryPresent, categoryBolded, currentCategorySelected);
        listView.setAdapter(adapter);

        adapter.swapItems(noteList);
        categoryManager = new CategoryManager(helper, categories, adapter, this);
        noteManager = new NoteManager(helper, categories, adapter, this, noteList, currentCategorySelected, atLeastOneCategoryPresent, categoryBolded);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Item Selected",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles all the UI events for the drawer. The drawer contains categories and actions for
     * those categories
     */
    public void setupDrawer(){
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(0xff3498db);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        NavigationView navigationView = findViewById(R.id.nav_view);

        Menu m = navigationView.getMenu();

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // clear
                        m.clear();

                        final int EDIT_CATEGORIES = 0;
                        m.addSubMenu(1, EDIT_CATEGORIES, m.NONE, "Category Options");

                        // add addCategory
                        final int ADD_CATEGORY = 1;
                        m.add(1 , ADD_CATEGORY, m.NONE, "Add a Category");

                        // add deleteCategory
                        final int DELETE_CATEGORY = 2;
                        m.add(1 , DELETE_CATEGORY, m.NONE, "Delete a Category");

                        // add all categories option
                        final int ALL_CATEGORIES = 3;
                        m.add(m.NONE , ALL_CATEGORIES, m.NONE, "All Categories");

                        // add categories to drawer
                        int i = 4;
                        for (String cat : categories){
                            m.add(m.NONE , i, m.NONE, cat);
                            i++;
                        }
                        drawerItemAmount = i;
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        if(menuItem.getItemId() == 1)
                            categoryManager.displayAddCategoryDialog();
                        else if(menuItem.getItemId() == 2){
                            categoryManager.displayDeleteCategoryDialog();
                        }
                        else if(menuItem.getItemId() == 3){
                            createListsFromDB();
                            currentCategorySelected = "All";
                            createListFromList(notes);

                        } else{
                            // get menuItem title/category
                            String category = menuItem.getTitle().toString();

                            // make a list of items in that category
                            List<Note> categoryNotes = new ArrayList<Note>();
                            // refresh screen
                            // change notes to all notes
                            createListsFromDB();
                            categoryNotes = categoryManager.makeCategoryList(category, notes);
                            currentCategorySelected = category;
                            createListFromList(categoryNotes);
                        }

                        return true;
                    }
                });
    }

    /**
     * Queries the DB to create a list of notes and categories. They contain the DB's data
     */
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
