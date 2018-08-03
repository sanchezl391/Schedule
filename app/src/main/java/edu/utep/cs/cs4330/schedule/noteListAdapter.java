package edu.utep.cs.cs4330.schedule;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class noteListAdapter extends ArrayAdapter<Note> {

    List<Note> notes;
    List<String> categories;
    Context ctx;
    int rsc;
    NoteListDatabaseHelper helper;AlertDialog.Builder builder;
    Parser parser = new Parser();
    boolean categoryBolded;
    boolean atLeastOneCategoryPresent;
    private String currentCategorySelected;
    Note newNote;

    /**
     * Initializes ItemAdapter
     * @param ctx The relevant context
     * @param rscId The template use to create a list of views
     * @param notes The list of notes that will be used to build the view
     */
    public noteListAdapter(Context ctx, int rscId, List<Note> notes, List<String> categories, NoteListDatabaseHelper helper, boolean atLeastOneCategoryPresent, boolean categoryBolded, String currentCategorySelected) {
        super(ctx, rscId, notes);
        this.rsc = rscId;
        this.notes = notes;
        this.categories = categories;
        this.ctx = ctx;
        this.helper = helper;
        this.atLeastOneCategoryPresent = atLeastOneCategoryPresent;
        this.categoryBolded = categoryBolded;
        this.currentCategorySelected = currentCategorySelected;
    }

    public void swapItems(List<Note> notes) {
        this.notes = notes;
        this.notifyDataSetChanged();
    }

    public void swapCategories(List<String> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    /**
     * Generates a view from the template and the view is modified with values of a note
     * @param position The position of the item within the adapter's data set of the item whose view we want.
     * @param view The old view to reuse, if possible
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View view, ViewGroup parent){
        LinearLayout noteListView;
        Note note = getItem(position);
        if(view == null){
            noteListView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            /**
             * Inflating means getting activity...template.xml and generating a view from it
             * This view object is returned and placed on the noteList activity
             */
            LayoutInflater vi;
            vi = (LayoutInflater)getContext().getSystemService(inflater);
            vi.inflate(rsc, noteListView, true);

        } else {
            noteListView = (LinearLayout) view;
        }

        TextView noteTitle = noteListView.findViewById(R.id.noteTitle);
        TextView noteCategory = noteListView.findViewById(R.id.noteCategory);
        TextView noteDate = noteListView.findViewById(R.id.noteDate);
        RadioButton deleteBtn = noteListView.findViewById(R.id.deleteBtn);
        LinearLayout editNoteArea = noteListView.findViewById(R.id.noteEditArea);

        noteTitle.setText(note.getTitle());
        noteCategory.setText(note.getCategory());
        noteDate.setText("");

        if(note.getTime().length() > 0){
            String[] splitArray = note.getTime().split("\\s+");
            String month = splitArray[1];
            String day = splitArray[2];
            String hour = splitArray[3];
            String minute = splitArray[4];
            if(minute.length() < 2)
                minute = "0" + minute;
            int intHour = Integer.parseInt(hour);
            String halfDay = "AM";
            if(intHour >= 12)
                halfDay = "PM";
            noteDate.setText(month + "/" + day + "    " + hour + ":" + minute + " " + halfDay);
        }

        setDeleteBtnListener(deleteBtn, position, note);
        setEditNoteListener(editNoteArea, note);
        return noteListView;
    }

    /**
     * this whole block of code is repeated!
     * @param editNoteArea
     */
    public void setEditNoteListener(LinearLayout editNoteArea, Note note){
        editNoteArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(ctx);
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);

                View dView = vi.inflate(R.layout.dialog_add_note, null);
                EditText title = (EditText) dView.findViewById(R.id.title);
                EditText body = (EditText) dView.findViewById(R.id.body);
                Button saveBtn = (Button) dView.findViewById(R.id.add);
                Button cancelBtn = (Button) dView.findViewById(R.id.cancel);

                title.setText(note.getTitle());
                body.setText(note.getBody());

                Calendar dateAndTime = setupTimeGUI(dView, note);

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

                builder.setView(dView);
                AlertDialog dialog = builder.create();
                dialog.show();

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String titleTxt = title.getText().toString();
                        String bodyTxt = body.getText().toString();

                        String category = parser.getKeyword(titleTxt, categories);

                        if(category.length() != 0 && !currentCategorySelected.equals("All"))
                            category = currentCategorySelected;

                        if(titleTxt.length() < 1 || bodyTxt.length() < 1){
                            Toast.makeText(ctx,"One or more fields are too short or empty", Toast.LENGTH_SHORT);
                        }
                        else {
                            boolean anotherCategory = !category.equals(currentCategorySelected);
                            boolean withinAllCategoriesAndNoKeyword = anotherCategory && currentCategorySelected.equals("All");

                            if(!anotherCategory || withinAllCategoriesAndNoKeyword) {
                                note.setTitle(titleTxt);
                                note.setBody(bodyTxt);
                                updateNote(note, category, dateAndTime);
                            }
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
        });
    }


    public void updateNote(Note note, String newCategory, Calendar dateAndTime){
        // first get index, must be exactly the same
        int i = -1;
        note.setCategory(newCategory);
        newNote = note;
        for (Note n : notes){
            if(note.getId() == n.getId()){
                i = notes.indexOf(n);

                n.setTitle(note.getTitle());
                n.setBody(note.getBody());
                newNote = n;
            }
        }

        String year = dateAndTime.get(dateAndTime.YEAR) + "";
        String month = dateAndTime.get(dateAndTime.MONTH) + "";
        String day = dateAndTime.get(dateAndTime.DAY_OF_MONTH) + "";
        String hour = dateAndTime.get(dateAndTime.HOUR_OF_DAY) + "";
        String minute = dateAndTime.get(dateAndTime.MINUTE) + "";
        String time = year + " " + month + " " + day + " " + hour + " " + minute;
        // ????
        note.setTime(time);

        notes.set(i, note);
//        notes.set(i, newNote);

        new Thread(() -> {
            // Do in background
            // We should parse in here

//            long rowId = helper.updateNote(newNote);
            long rowId = helper.updateNote(note);

            ((Activity) ctx).runOnUiThread(() -> { // UI
                if (rowId > 0) {
                    WakefulReceiver receiver = new WakefulReceiver();
                    receiver.cancelAlarm(ctx, note);
                    receiver.setAlarm(ctx, note);
                    swapItems(notes);
                    Log.e("Success: ", "DB Add Operation Succeeded");
                } else {
                    Log.e("Error: ", "DB Add Operation failed");
                }
            });
        }).start();
    }

    public void setDeleteBtnListener(Button deleteBtn, int index, Note note){
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(note);
            }
        });
    }

//    public void setMenuOptionsClickListener(PopupMenu menu, int index, Note note) {
//        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem option) {
//                switch(option.getItemId()){
//                    case R.id.deleteItemOption:
//                        deleteNote(note);
//                        return true;
//                }
//
//                return false;
//            }
//
//        });
//    }

    public void deleteNote(Note note){
        new Thread(() -> {
            // Do in background
            long success = helper.deleteNote(note.getId());

            ((Activity) ctx).runOnUiThread(() -> { // UI
                if (success > 0) {
                    if(note.getTime().length() > 0){
                        WakefulReceiver receiver = new WakefulReceiver();
                        receiver.cancelAlarm(ctx, note);
                    }
                    notes.remove(note);
                    swapItems(notes);
                    Log.e("Success: ", "DB Add Operation Succeeded");
                } else {
                    Log.e("Error: ", "DB Add Operation failed");
                }
            });
        }).start();
    }

    public Calendar setupTimeGUI(View view, Note note){
        Button timeBtn = (Button) view.findViewById(R.id.time);
        Calendar currentTime = Calendar.getInstance();

        int dayOfMonth;
        int month;
        int hour;
        int minute;
        int year = currentTime.get(Calendar.YEAR);



        if(note.getTime().length() > 0){
            String[] splitArray = note.getTime().split("\\s+");
            month = Integer.parseInt(splitArray[1]) - 1;
            dayOfMonth = Integer.parseInt(splitArray[2]);
            hour = Integer.parseInt(splitArray[3]);
            minute = Integer.parseInt(splitArray[4]);
        } else {
            hour = currentTime.get(Calendar.HOUR_OF_DAY);
            minute = currentTime.get(Calendar.MINUTE);
            dayOfMonth = currentTime.get(Calendar.DAY_OF_MONTH);
            month = currentTime.get(Calendar.MONTH);
        }




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
//                                formatTime(hourOfDay);
                                Log.e("Hour: ", hourOfDay + "");
                                Log.e("Minute: ", minute + "");
//                        timeBtn.setText(hourOfDay + " : " + minute + " " + format);
                                currentTime.set(year, month + 1 , dayOfMonth, hourOfDay, minute);
                            }
                        },hour, minute, true);
                        timePickerDialog.show();
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();


            }
        });
        return currentTime;
    }
}






















