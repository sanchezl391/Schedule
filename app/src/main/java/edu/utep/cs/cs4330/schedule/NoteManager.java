package edu.utep.cs.cs4330.schedule;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class NoteManager {
    private AlertDialog.Builder builder;
    private NoteListDatabaseHelper helper;
    private noteListAdapter adapter;
    private List<String> categories;
    private List<Note> notes;
    private Context ctx;
    private Parser parser = new Parser();
    private String currentCategorySelected;
    private boolean atLeastOneCategoryPresent;
    private boolean categoryBolded;

    public NoteManager(NoteListDatabaseHelper helper, List<String> categories, noteListAdapter adapter, Context ctx, List<Note> notes, String currentCategorySelected, boolean atLeastOneCategoryPresent, boolean categoryBolded){
        this.helper = helper;
        this.categories = categories;
        this.adapter = adapter;
        this.ctx = ctx;
        this.notes = notes;
        this.currentCategorySelected = currentCategorySelected;
        this.atLeastOneCategoryPresent = atLeastOneCategoryPresent;
        this.categoryBolded = categoryBolded;
    }

    protected void addNote(String title, String body){
        new Thread(() -> {
            // Do in background
            String category = parser.getKeyword(title, categories);

            //this is for when creating notes within a category view
//            if(category.length() == 0)
//                category = currentCategorySelected;


            Note note = new Note(title, body, category, "Today");

            long rowId = helper.addItem(note);

            ((Activity) ctx).runOnUiThread(() -> { // UI
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
        builder = new AlertDialog.Builder(ctx);
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dView = li.inflate(R.layout.dialog_add_note, null);
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

                        ((Activity) ctx).runOnUiThread(() -> { // UI
                            // get indices
                            final int index = s.toString().toLowerCase().indexOf(category.toLowerCase());
                            final int length = category.length();

                            title.removeTextChangedListener(this);

                            final SpannableStringBuilder sb = new SpannableStringBuilder(s.toString());

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
                    Toast.makeText(ctx,"One or more fields are too short or empty", Toast.LENGTH_SHORT);
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
