package sk.ttomovcik.quickly.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import sk.ttomovcik.quickly.MainActivity;
import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.db.NotesDb;

import static sk.ttomovcik.quickly.R.id.btn_bottomsheet_delete;
import static sk.ttomovcik.quickly.R.id.eFab_bottomsheet_done;
import static sk.ttomovcik.quickly.R.id.textBox_bottomsheet_noteTextTextBox;
import static sk.ttomovcik.quickly.R.id.textBox_bottomsheet_noteTitleTextBox;

public class AddNoteBottomModalSheet extends BottomSheetDialogFragment {

    private NotesDb notesDb;
    private boolean isInEditMode = false;
    private ExtendedFloatingActionButton btn_save;
    private AppCompatButton btn_delete;
    private String bundleId, bundleTitle, bundleText;
    private TextInputEditText inputTitle, inputText;

    public static AddNoteBottomModalSheet newInstance() {
        return new AddNoteBottomModalSheet();
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_addnote, container, false);

        Bundle bundle = this.getArguments();
        isInEditMode = bundle != null && bundle.getBoolean("setEditMode");
        notesDb = new NotesDb(getContext());

        // Assign variables
        btn_delete = view.findViewById(btn_bottomsheet_delete);
        btn_save = view.findViewById(eFab_bottomsheet_done);
        inputTitle = view.findViewById(textBox_bottomsheet_noteTitleTextBox);
        inputText = view.findViewById(textBox_bottomsheet_noteTextTextBox);


        // Create onClickListeners for buttons
        if (isInEditMode) {
            assert bundle != null;
            bundleId = bundle.getString("id");
            bundleTitle = bundle.getString("title");
            bundleText = bundle.getString("text");
            populateData();
            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setOnClickListener(v -> {
                notesDb.deleteNote(bundleId);
                ((MainActivity) Objects.requireNonNull(getActivity())).loadRecyclerView("allExceptArchived");
                dismiss();
            });
            btn_save.setOnClickListener(v -> {
                notesDb.updateItem(bundleId, "title", getInput()[0]);
                notesDb.updateItem(bundleId, "text", getInput()[1]);
                ((MainActivity) Objects.requireNonNull(getActivity())).loadRecyclerView("allExceptArchived");
                dismiss();
            });
        } else {
            btn_save.setOnClickListener(v -> {
                notesDb.addNote(getInput()[0], getInput()[1], "normal");
                ((MainActivity) Objects.requireNonNull(getActivity())).loadRecyclerView("allExceptArchived");
                dismiss();
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

    private String[] getInput() {
        return new String[]{
                Objects.requireNonNull(inputTitle.getText()).toString(),
                Objects.requireNonNull(inputText.getText()).toString()
        };
    }

    private void populateData() {
        inputTitle.setText(bundleTitle);
        inputText.setText(bundleText);
    }
}