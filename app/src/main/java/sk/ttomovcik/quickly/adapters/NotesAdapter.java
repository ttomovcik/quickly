package sk.ttomovcik.quickly.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.activities.AddNote;
import sk.ttomovcik.quickly.model.Note;

import static sk.ttomovcik.quickly.R.layout.layout_item_note;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private List<Note> noteList;
    private Context context;

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView title, text;
        FloatingActionButton colorTag;
        RelativeLayout notesItem;

        NotesViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tv_notesItem_title);
            text = view.findViewById(R.id.tv_notesItem_text);
            colorTag = view.findViewById(R.id.chip_notesItem_color);
            notesItem = view.findViewById(R.id.notesItem_item);
        }
    }

    public NotesAdapter(Context context, List<Note> noteList) {
        this.noteList = noteList;
        this.context = context;
    }

    @NotNull
    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout_item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {
        Note model = noteList.get(position);

        holder.title.setText(model.getTitle());
        holder.text.setText(model.getText());
        if (!model.getColor().contains("C471ED")) {
            holder.colorTag.setVisibility(View.VISIBLE);
            holder.colorTag.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(model.getColor())));
        }
        holder.notesItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddNote.class);
            intent.putExtra("_id", model.get_Id());
            intent.putExtra("_title", model.getTitle());
            intent.putExtra("_text", model.getText());
            intent.putExtra("_color", model.getColor());
            intent.putExtra("_state", model.getState());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
