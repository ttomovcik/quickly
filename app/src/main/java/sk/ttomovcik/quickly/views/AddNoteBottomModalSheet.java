package sk.ttomovcik.quickly.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import static sk.ttomovcik.quickly.R.id.tv_bottomsheet_titleAddNote;

public class AddNoteBottomModalSheet extends BottomSheetDialogFragment {

    private NotesDb notesDb;
    private boolean isInEditMode = false;
    private ExtendedFloatingActionButton btn_save;
    private FloatingActionButton btn_delete, btn_archive;
    private String bundleId, bundleTitle, bundleText;
    private TextInputEditText inputTitle, inputText;
    private TextView dialogTitle;

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
        dialogTitle = view.findViewById(tv_bottomsheet_titleAddNote);
        btn_delete = view.findViewById(btn_bottomsheet_delete);
        btn_archive = view.findViewById(R.id.btn_bottomsheet_archive);
        btn_save = view.findViewById(eFab_bottomsheet_done);
        inputTitle = view.findViewById(textBox_bottomsheet_noteTitleTextBox);
        inputText = view.findViewById(textBox_bottomsheet_noteTextTextBox);


        // Create onClickListeners for buttons
        if (isInEditMode) {
            // Get data from bundle
            assert bundle != null;
            bundleId = bundle.getString("id");
            bundleTitle = bundle.getString("title");
            bundleText = bundle.getString("text");

            // Change title to 'edit note'
            dialogTitle.setText(R.string.edit_note);

            // Set text from db
            populateData();

            btn_delete.setVisibility(View.VISIBLE);
            btn_delete.setOnClickListener(v -> {
                notesDb.deleteNote(bundleId);
                reloadAndExit();
            });

            btn_archive.setVisibility(View.VISIBLE);
            btn_archive.setOnClickListener(v -> {
                notesDb.updateItem(bundleId, "state", "archived");
                reloadAndExit();
            });

            // Change save button to update instead of saving new note
            btn_save.setOnClickListener(v -> {
                notesDb.updateItem(bundleId, "title", getInput()[0]);
                notesDb.updateItem(bundleId, "text", getInput()[1]);
                reloadAndExit();
            });
        } else {
            btn_save.setOnClickListener(v -> {
                notesDb.addNote(getInput()[0], getInput()[1], "normal");
                reloadAndExit();
            });
        }
        return view;
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        // Basically a cheat-y way to hide keyboard
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * Returns title and text from editTextBoxes
     *
     * @return String[] {title, text}
     */
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

    private void reloadAndExit() {
        ((MainActivity) Objects.requireNonNull(getActivity())).prepareRecyclerView("allExceptArchived");
        dismiss();
    }
}