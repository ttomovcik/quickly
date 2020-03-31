package sk.ttomovcik.quickly.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.activities.AddNote;
import sk.ttomovcik.quickly.activities.MainActivity;
import sk.ttomovcik.quickly.db.NotesDb;
import sk.ttomovcik.quickly.helpers.TextHelpers;
import sk.ttomovcik.quickly.model.Note;

import static sk.ttomovcik.quickly.R.id.btn_restoreNote;
import static sk.ttomovcik.quickly.R.id.chip_notesItem_color;
import static sk.ttomovcik.quickly.R.id.ll_divider;
import static sk.ttomovcik.quickly.R.id.notesItem_item;
import static sk.ttomovcik.quickly.R.id.tv_notesItem_text;
import static sk.ttomovcik.quickly.R.id.tv_notesItem_title;
import static sk.ttomovcik.quickly.R.layout.layout_item_note;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<Note> noteList;
    private NotesDb notesDb;
    private Context context;
    private final static String TAG = "NotesAdapter";

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView title, text;
        FloatingActionButton colorTag;
        RelativeLayout notesItem;
        LinearLayout divider;
        MaterialButton btn_restore;

        NotesViewHolder(View view) {
            super(view);
            title = view.findViewById(tv_notesItem_title);
            text = view.findViewById(tv_notesItem_text);
            colorTag = view.findViewById(chip_notesItem_color);
            notesItem = view.findViewById(notesItem_item);
            divider = view.findViewById(ll_divider);
            btn_restore = view.findViewById(btn_restoreNote);
        }
    }

    public NotesAdapter(Context context, List<Note> noteList) {
        this.noteList = noteList;
        this.context = context;
    }

    @NotNull
    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        notesDb = new NotesDb(context);
        return new NotesViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_item_note, parent, false));
    }

    @SuppressLint("SetTextI18n") @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {
        Note noteModel = noteList.get(position);

        // Set title and text
        holder.title.setText(noteModel.getTitle());
        holder.text.setText(noteModel.getText());

        // Show a color badge (if any)
        if (!new TextHelpers().isEmpty(noteModel.getColor())) {
            holder.colorTag.setVisibility(View.VISIBLE);
            holder.colorTag.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(noteModel.getColor())));
        }

        // Show button to restore notes. Visible only white filtering
        if (noteModel.getState().contains("archived")) {
            holder.divider.setVisibility(View.VISIBLE);
            holder.btn_restore.setVisibility(View.VISIBLE);
            holder.btn_restore.setText(context.getString(R.string.restore_note) + " " + noteModel.getText());
            holder.btn_restore.setOnClickListener(v -> {
                notesDb.updateItem(noteModel.get_Id(), "state", "normal");
                noteList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                if (noteList.size() == 0) {
                    ((MainActivity) context).showFilterNotification(false);
                    ((MainActivity) context).loadRecyclerView("allExceptArchived", "");
                }
            });
        }

        // Set onClickListener
        holder.notesItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddNote.class);
            intent.putExtra("_action", "1");
            intent.putExtra("_noteId", String.valueOf(noteModel.get_Id()));
            intent.putExtra("_title", noteModel.getTitle());
            intent.putExtra("_text", noteModel.getText());
            intent.putExtra("_color", noteModel.getColor());
            intent.putExtra("_state", noteModel.getState());
            intent.putExtra("_lastEdited", noteModel.getLastEdited());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
