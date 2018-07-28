package edu.utep.cs.cs4330.schedule;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.util.List;

public class noteListAdapter extends ArrayAdapter<Note> {

    List<Note> notes;
    Context ctx;
    int rsc;
    NoteListDatabaseHelper helper;

    /**
     * Initializes ItemAdapter
     * @param ctx The relevant context
     * @param rscId The template use to create a list of views
     * @param notes The list of notes that will be used to build the view
     */
    public noteListAdapter(Context ctx, int rscId, List<Note> notes, NoteListDatabaseHelper helper) {
        super(ctx, rscId, notes);
        this.rsc = rscId;
        this.notes = notes;
        this.ctx = ctx;
        this.helper = helper;
    }

    public void swapItems(List<Note> notes) {
        this.notes = notes;
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
        Button optionsBtn = noteListView.findViewById(R.id.optionsBtn);

        noteTitle.setText(note.getTitle());
        noteCategory.setText(note.getCategory());
        noteDate.setText(note.getDate());
        setOptionsBtnListener(optionsBtn, position, note);

        return noteListView;
    }

    public void setOptionsBtnListener(Button optionsBtn, int index, Note note){
        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getContext(), v);
                setMenuOptionsClickListener(popup, index, note);
                popup.inflate(R.menu.options_menu);
                popup.show();
            }
        });
    }

    public void setMenuOptionsClickListener(PopupMenu menu, int index, Note note) {
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem option) {
                switch(option.getItemId()){
                    case R.id.deleteItemOption:
                        deleteNote(note);
                        return true;
                }

                return false;
            }

        });
    }

    public void deleteNote(Note note){
        new Thread(() -> {
            // Do in background
            long success = helper.deleteNote(note.getId());

            ((Activity) ctx).runOnUiThread(() -> { // UI
                if (success > 0) {
                    notes.remove(note); // We might not be updating the activity's list
                    swapItems(notes);
                    Log.e("Success: ", "DB Add Operation Succeeded");
                } else {
                    Log.e("Error: ", "DB Add Operation failed");
                }
            });
        }).start();
    }

    public void editNote(Note note){
        notes.set(notes.indexOf(note), note);

        new Thread(() -> {
            // Do in background
            int success = helper.updateNote(note);

            ((Activity) ctx).runOnUiThread(() -> { // UI
                if (success != -1) {
                    swapItems(notes);
                    Log.e("Success: ", "DB Add Operation Succeeded");
                } else {
                    Log.e("Error: ", "DB Add Operation failed");
                }
            });
        }).start();
    }



}






















