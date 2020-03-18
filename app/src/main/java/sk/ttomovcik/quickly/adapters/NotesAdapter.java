package sk.ttomovcik.quickly.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import sk.ttomovcik.quickly.MainActivity;
import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.db.NotesDb;
import sk.ttomovcik.quickly.model.Note;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<Note> noteList;
    private NotesDb notesDb;
    private Context context;
    private View itemView;

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView title, text;
        FloatingActionButton fab_favorite;
        CardView cv_notesItem_cardView;
        String favorite;

        NotesViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_notesItem_title);
            text = view.findViewById(R.id.tv_notesItem_text);
            fab_favorite = view.findViewById(R.id.fab_notesItem_favorite);
            cv_notesItem_cardView = view.findViewById(R.id.cv_notesItem_cardView);
        }
    }

    public NotesAdapter(List<Note> noteList, Context context) {
        this.noteList = noteList;
        this.context = context;
    }

    @NotNull
    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.layout_notescard, parent, false);
        notesDb = new NotesDb(parent.getContext());
        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.cv_notesItem_cardView.setOnClickListener(view -> ((MainActivity) context)
                .openNoteInEditMode(String.valueOf(note.get_Id()),
                        note.getTitle(), note.getText()));
        holder.title.setText(note.getTitle());
        if (note.getState().equals("favorite")) {
            holder.fab_favorite.setImageResource(R.drawable.ic_favorite_24dp);
        } else {
            holder.fab_favorite.setImageResource(R.drawable.ic_favorite_border_24dp);
        }
        if (note.getState().equals("archived")) {
            holder.fab_favorite.setImageResource(R.drawable.ic_restore_24dp);
        }
        holder.text.setText(note.getText());
        holder.fab_favorite.setOnClickListener(v -> {
            if (note.getState().contains("normal")) {
                notesDb.updateItem(String.valueOf(note.get_Id()), "state", "favorite");
                if (context instanceof MainActivity) {
                    Snackbar.make(v, context.getString(R.string.added_to_favorites), Snackbar.LENGTH_SHORT).show();
                    ((MainActivity) context).prepareRecyclerView();
                }
            }
            if (note.getState().contains("favorite")) {
                notesDb.updateItem(String.valueOf(note.get_Id()), "state", "normal");
                if (context instanceof MainActivity) {
                    Snackbar.make(v, context.getString(R.string.removed_from_favorites), Snackbar.LENGTH_SHORT).show();
                    ((MainActivity) context).prepareRecyclerView();
                }
            }
            if (note.getState().contains("archived")) {
                notesDb.updateItem(String.valueOf(note.get_Id()), "state", "normal");
                if (context instanceof MainActivity) {
                    Snackbar.make(v, context.getString(R.string.restored), Snackbar.LENGTH_SHORT).show();
                    ((MainActivity) context).prepareRecyclerView();
                }
            }
        });
    }

    public void archiveNote(int position) {
        notesDb.updateItem(String.valueOf(Math.floor(position + 1)), "state", "archived");
        Snackbar.make(itemView, context.getString(R.string.note_archived), Snackbar.LENGTH_SHORT).show();
        ((MainActivity) context).prepareRecyclerView();
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
