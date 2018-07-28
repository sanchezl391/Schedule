package edu.utep.cs.cs4330.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

public class noteListAdapter extends ArrayAdapter<Note> {

    List<Note> notes;
    Context ctx;
    int rsc;

    /**
     * Initializes ItemAdapter
     * @param ctx The relevant context
     * @param rscId The template use to create a list of views
     * @param notes The list of notes that will be used to build the view
     */
    public noteListAdapter(Context ctx, int rscId, List<Note> notes) {
        super(ctx, rscId, notes);
        this.rsc = rscId;
        this.notes = notes;
        this.ctx = ctx;
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

        noteTitle.setText(note.getTitle());
        noteCategory.setText(note.getCategory());
        noteDate.setText(note.getDate());


        return noteListView;
    }




}
