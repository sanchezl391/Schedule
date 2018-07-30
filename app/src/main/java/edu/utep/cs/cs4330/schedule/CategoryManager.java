package edu.utep.cs.cs4330.schedule;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CategoryManager {
    private AlertDialog.Builder builder;
    private NoteListDatabaseHelper helper;
    private noteListAdapter adapter;
    private List<String> categories;
    private Context ctx;

    public CategoryManager(NoteListDatabaseHelper helper, List<String> categories, noteListAdapter adapter, Context ctx){
        this.helper = helper;
        this.categories = categories;
        this.adapter = adapter;
        this.ctx = ctx;
    }

    protected List<Note> makeCategoryList(String category, List<Note> notes){
        List<Note> newNotes =  new ArrayList<Note>();
        for (Note note : notes) {
            if(note.getCategory().equals(category))
                newNotes.add(note);
        }
        return newNotes;
    }

    public void displayDeleteCategoryDialog(){
        builder = new AlertDialog.Builder(ctx);
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dView = li.inflate(R.layout.dialog_add_category, null);

        TextView title = dView.findViewById(R.id.categoryTitle);
        title.setText("Delete A Category");
        EditText category = (EditText) dView.findViewById(R.id.categoryName);
        Button deleteBtn = (Button) dView.findViewById(R.id.categoryAddBtn);
        deleteBtn.setText("Delete");
        Button cancelBtn = (Button) dView.findViewById(R.id.categoryCancelBtn);
        builder.setView(dView);
        AlertDialog dialog = builder.create();
        dialog.show();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = category.getText().toString();

                if(categoryName.length() < 1){
                    Toast.makeText(ctx,"Please enter a name for category", Toast.LENGTH_SHORT);
                }
                else {
                    deleteCategory(categoryName);
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

    public void deleteCategory(String category){
        new Thread(() -> {
            // Do in background
            // We should parse in here

            long rowId = helper.deleteCategory(category);

            ((Activity) ctx).runOnUiThread(() -> { // UI
                if (rowId != -1) {
                    categories.remove(category);
                    adapter.swapCategories(categories);
                    Log.e("Success: ", "DB Add Operation Succeeded");
                } else {
                    Log.e("Error: ", "DB Add Operation failed");
                }
            });
        }).start();
    }

    public void displayAddCategoryDialog(){
        builder = new AlertDialog.Builder(ctx);
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dView = li.inflate(R.layout.dialog_add_category, null);
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
                    Toast.makeText(ctx,"Please enter a name for category", Toast.LENGTH_SHORT);
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

    public void addCategory(String category){
        new Thread(() -> {
            // Do in background
            // We should parse in here

            long rowId = helper.addCategory(category);

            ((Activity) ctx).runOnUiThread(() -> { // UI
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
}
