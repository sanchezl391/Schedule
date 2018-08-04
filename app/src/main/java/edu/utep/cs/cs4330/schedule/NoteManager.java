package edu.utep.cs.cs4330.schedule;

import android.app.Activity;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
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

    protected void addNote(String title, String body, Calendar dateAndTime, Button clearNotificationBtn){

        new Thread(() -> {

            // Do in background
            String category = parser.getKeyword(title, categories);

            //this is for when creating notes within a category view
            if(category.length() == 0 && !currentCategorySelected.equals("All"))
                category = currentCategorySelected;

            Note note = new Note(title, body, category, "Today");

            String year;
            String month;
            String day;
            String hour;
            String minute;
            String time = "";

            String notificationStatus = clearNotificationBtn.getText().toString();
            boolean notificationsEnabled = !notificationStatus.equals("CLEAR NOTIFICATIONS") || notificationStatus.equals("CLEAR NOTIFICATION");

            if(dateAndTime.get(Calendar.ERA) != 0 || notificationsEnabled ){
                year = dateAndTime.get(dateAndTime.YEAR) + "";
                month = dateAndTime.get(dateAndTime.MONTH) + "";
                day = dateAndTime.get(dateAndTime.DAY_OF_MONTH) + "";
                hour = dateAndTime.get(dateAndTime.HOUR_OF_DAY) + "";
                minute = dateAndTime.get(dateAndTime.MINUTE) + "";
                time = year + " " + month + " " + day + " " + hour + " " + minute;
            }




            note.setTime(time);









            long rowId = helper.addItem(note);

            if(rowId != -1){
                note.setId((int)rowId);
                notes.add(note);

                if(time.length() > 0){
                    WakefulReceiver receiver = new WakefulReceiver();
                    receiver.setAlarm(ctx, note);
                }
            }
            // Have to have better way of handling
            boolean anotherCategory = !category.equals(currentCategorySelected);
            boolean withinAllCategoriesAndNoKeyword = anotherCategory && currentCategorySelected.equals("All");


            ((Activity) ctx).runOnUiThread(() -> { // UI
                if (!anotherCategory || withinAllCategoriesAndNoKeyword) {
//                    note.setId((int)rowId);
//                    notes.add(note);
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
                            final int length = category.length() + index;

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


        Calendar dateAndTime = setupTimeGUI(dView);






        Button clearNotificationBtn = dView.findViewById(R.id.clearNotificationBtn);
        clearNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearNotificationBtn.setText("CLEAR NOTIFICATION");
                Toast.makeText(ctx, "Notifications removed for this note", Toast.LENGTH_SHORT);
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

                if(titleTxt.length() < 1 ){
                    Snackbar.make(view, "Title is empty. Please Try again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
//                    addNote(titleTxt, bodyTxt, dateAndTime);
                    addNote(titleTxt, bodyTxt, dateAndTime, clearNotificationBtn);
                    Snackbar.make(view, "Item added successfully", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

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

    public String formatTime(int hour){
        String format;
      if(hour == 0){
          hour+=12;
          format = "AM";
      } else if(hour == 12){
          format = "PM";
      } else if(hour > 12){
        hour-= 12;
        format = "PM";
      } else {
          format = "AM";
      }
        return format;
    }

    public Calendar setupTimeGUI(View view){
        Button timeBtn = (Button) view.findViewById(R.id.time);
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(Calendar.ERA, 0);

        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        int dayOfMonth = currentTime.get(Calendar.DAY_OF_MONTH);
        int month = currentTime.get(Calendar.MONTH) + 1;
        int year = currentTime.get(Calendar.YEAR);

//        String format = formatTime(hour);
//        timeBtn.setText(hour + " : " + minute + " " + format);
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                DatePickerDialog datePickerDialog = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        TimePickerDialog timePickerDialog = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                formatTime(hourOfDay);
                                Log.e("Hour: ", hourOfDay + "");
                                Log.e("Minute: ", minute + "");
//                        timeBtn.setText(hourOfDay + " : " + minute + " " + format);

                                currentTime.set(year, month + 1, dayOfMonth, hourOfDay, minute);
                                currentTime.set(Calendar.ERA, 1);

                            }
                        },hour, minute, true);
                        timePickerDialog.show();
                    }
                }, year, month - 1, dayOfMonth);
                datePickerDialog.show();


            }
        });

        return currentTime;
    }

}
