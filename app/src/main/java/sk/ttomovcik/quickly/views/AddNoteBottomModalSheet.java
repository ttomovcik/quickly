package sk.ttomovcik.quickly.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import sk.ttomovcik.quickly.MainActivity;
import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.db.NotesDb;

public class AddNoteBottomModalSheet extends BottomSheetDialogFragment {

    private TextInputEditText inputTitle, inputText;
    private String title, text;
    private NotesDb notesDb;
    private int id;

    public static AddNoteBottomModalSheet newInstance() {
        return new AddNoteBottomModalSheet();
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_addnote, container, false);

        Bundle bundle = this.getArguments();
        notesDb = new NotesDb(getContext());

        inputTitle = view.findViewById(R.id.textBox_bottomsheet_noteTitleTextBox);
        inputText = view.findViewById(R.id.textBox_bottomsheet_noteTextTextBox);
        AppCompatButton btn_delete = view.findViewById(R.id.btn_bottomsheet_delete);

        if (bundle != null && bundle.getBoolean("setEditMode")) {
            inputTitle.setText(bundle.getString("title"));
            inputText.setText(bundle.getString("text"));
            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setOnClickListener(v -> {
                notesDb.deleteNote(bundle.getString("id"));
                ((MainActivity) Objects.requireNonNull(getActivity())).loadRecyclerView("allExceptArchived");
                dismiss();
            });
            view.findViewById(R.id.eFab_bottomsheet_done).setOnClickListener(v -> {
                title = Objects.requireNonNull(inputTitle.getText()).toString();
                text = Objects.requireNonNull(inputText.getText()).toString();
                if (isEmpty(title) || isEmpty(text)) {
                    notesDb.updateItem(bundle.getString("id"), "title", title);
                    notesDb.updateItem(bundle.getString("id"), "text", text);
                } else {
                    dismiss();
                }
                dismiss();
                ((MainActivity) Objects.requireNonNull(getActivity())).loadRecyclerView("allExceptArchived");
            });
        } else {
            view.findViewById(R.id.eFab_bottomsheet_done).setOnClickListener(v -> {
                title = Objects.requireNonNull(inputTitle.getText()).toString();
                text = Objects.requireNonNull(inputText.getText()).toString();
                if (isEmpty(title) || isEmpty(text)) {
                    notesDb.addNote(title, text, "normal");
                } else {
                    dismiss();
                }
                dismiss();
                ((MainActivity) Objects.requireNonNull(getActivity())).loadRecyclerView("allExceptArchived");
            });
        }

        return view;
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }
}