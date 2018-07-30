package edu.utep.cs.cs4330.schedule;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class noteList extends AppCompatActivity {
    ListView listView;
    List<Note> notes;
//    List<Note> categoryNotes;
    List<String> categories;
    NoteListDatabaseHelper helper;
    Button addNoteBtn;
    noteListAdapter adapter;
    AlertDialog.Builder builder;
    private DrawerLayout mDrawerLayout;
    private boolean atLeastOneCategoryPresent = false;
    private boolean categoryBolded = false;
    private int drawerItemAmount = 0;
    private String currentCategorySelected = "";
    private Parser parser = new Parser();
    private CategoryManager categoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        helper = new NoteListDatabaseHelper(this, 2);

        notes = new ArrayList<Note>();
//        categoryNotes = new ArrayList<Note>();
        categories = new ArrayList<String>();

        createListsFromDB();
        createListFromList(notes);

        addNoteBtn = findViewById(R.id.addBtn);
        addNoteBtn.setOnClickListener(this::displayAddNoteDialog);
        setupDrawer();
        categoryManager = new CategoryManager(helper, categories, adapter, this);

//        NavigationView navigationView = findViewById(R.id.drawer_layout);
//        Spinner spinner = (Spinner) navigationView.getMenu().findItem(R.id.navigation_drawer_item3).getActionView();
//        spinner.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,language));
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this,language[position],Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }

    public void createListFromList(List<Note> noteList){
        listView = findViewById(R.id.listView);
        adapter = new noteListAdapter(this, R.layout.list_note_template, noteList, categories, helper);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Item Selected",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setupDrawer(){
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
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

                        // add addCategory
                        final int ADD_CATEGORY = 0;
                        m.add(m.NONE , ADD_CATEGORY, m.NONE, "Add a Category");

                        // add deleteCategory
                        final int DELETE_CATEGORY = 1;
                        m.add(m.NONE , DELETE_CATEGORY, m.NONE, "Delete a Category");

                        // add all categories option
                        final int ALL_CATEGORIES = 2;
                        m.add(m.NONE , ALL_CATEGORIES, m.NONE, "All Categories");

                        // add categories to drawer
                        int i = 3;
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

                        if(menuItem.getItemId() == 0)
                            displayAddCategoryDialog();
                        else if(menuItem.getItemId() == 1){
                            categoryManager.displayDeleteCategoryDialog();
                        }
                        else if(menuItem.getItemId() == 2){
                            createListsFromDB();
                            createListFromList(notes);
                        } else{
                            // get menuItem title/category
                            String category = menuItem.getTitle().toString();

                            // make a list of items in that category
                            List<Note> categoryNotes = new ArrayList<Note>();
                            // refresh screen
                            categoryNotes = categoryManager.makeCategoryList(category, notes);
                            currentCategorySelected = category;
                            createListFromList(categoryNotes);
                        }

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







//    public void displayDeleteCategoryDialog(){ //
//        builder = new AlertDialog.Builder(this);
//
//        View dView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
//        TextView title = dView.findViewById(R.id.categoryTitle);
//        title.setText("Delete A Category");
//        EditText category = (EditText) dView.findViewById(R.id.categoryName);
//        Button deleteBtn = (Button) dView.findViewById(R.id.categoryAddBtn);
//        deleteBtn.setText("Delete");
//        Button cancelBtn = (Button) dView.findViewById(R.id.categoryCancelBtn);
//        builder.setView(dView);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String categoryName = category.getText().toString();
//
//                if(categoryName.length() < 1){
//                    Toast.makeText(getApplicationContext(),"Please enter a name for category", Toast.LENGTH_SHORT);
//                }
//                else {
//                    deleteCategory(categoryName);
//                }
//                dialog.dismiss();
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//    }

    public void displayAddCategoryDialog(){
        builder = new AlertDialog.Builder(this);

        View dView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        EditText category = (EditText) dView.findViewById(R.id.categoryName);
        Button addBtn = (Button) dView.findViewById(R.id.categoryAddBtn);
        Button cancelBtn = (Button) dView.findViewById(R.id.categoryCancelBtn);
        builder.setView(dView);
        AlertDialog dialog = builder.create();
        dialog.show();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = category.getText().toString();

                if(categoryName.length() < 1){
                    Toast.makeText(getApplicationContext(),"Please enter a name for category", Toast.LENGTH_SHORT);
                }
                else {
                    addCategory(categoryName);
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

//    public void deleteCategory(String category){
//        new Thread(() -> {
//            // Do in background
//            // We should parse in here
//
//            long rowId = helper.deleteCategory(category);
//
//            runOnUiThread(() -> { // UI
//                if (rowId != -1) {
//                    categories.remove(category);
//                    adapter.swapCategories(categories);
//                    Log.e("Success: ", "DB Add Operation Succeeded");
//                } else {
//                    Log.e("Error: ", "DB Add Operation failed");
//                }
//            });
//        }).start();
//    }

    public void addCategory(String category){
        new Thread(() -> {
            // Do in background
            // We should parse in here

            long rowId = helper.addCategory(category);

            runOnUiThread(() -> { // UI
                if (rowId != -1) {
                    categories.add(category);
                    adapter.swapCategories(categories);
                    Log.e("Success: ", "DB Add Operation Succeeded");
                } else {
                    Log.e("Error: ", "DB Add Operation failed");
                }
            });
        }).start();
    }





    public void addNote(String title, String body){
        new Thread(() -> {
            // Do in background
            // We should parse in here
//            parseNote(title, body);
            String category = parser.getKeyword(title, categories);
            if(category.length() == 0)
                category = currentCategorySelected;
//            else if(!category.equals(currentCategorySelected)){
//                notes = makeCategoryList(category);
//                currentCategorySelected = category;
//                createListFromList();
//            }

            Note note = new Note(title, body, category, "Today");

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

    protected void displayAddNoteDialog(View view){
        builder = new AlertDialog.Builder(this);

        View dView = getLayoutInflater().inflate(R.layout.dialog_add_note, null);
        EditText title = (EditText) dView.findViewById(R.id.title);

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String category = parser.getKeyword(s.toString(), categories);
                StyleSpan bss;
                bss = new StyleSpan(Typeface.NORMAL);
                if(category.length() > 0)
                    atLeastOneCategoryPresent = true;
                else {
                    atLeastOneCategoryPresent = false;

                    // remove bolding
                    categoryBolded = false;
                }


                if(atLeastOneCategoryPresent ){ // doesnt get executed since theres no category
                    new Thread(() -> {
                        // Do in background
                        // We should parse in here

                        runOnUiThread(() -> { // UI
                            // get indices
                            final int index = s.toString().toLowerCase().indexOf(category.toLowerCase());
                            final int length = category.length();

                            title.removeTextChangedListener(this);

                            final SpannableStringBuilder sb = new SpannableStringBuilder(s.toString());

//                    final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
                            if (categoryBolded)
                                sb.setSpan(new StyleSpan(Typeface.BOLD), index, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            else
                                sb.setSpan(bss, index, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            title.setText(sb);
                            title.setSelection(s.length());

                            Log.e("Title: ", s.toString());

                            title.addTextChangedListener(this);
                            categoryBolded = true;
                        });
                    }).start();
                }



            }
        });



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







}
