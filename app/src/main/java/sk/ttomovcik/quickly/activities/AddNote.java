package sk.ttomovcik.quickly.activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.db.NotesDb;
import sk.ttomovcik.quickly.helpers.TextHelpers;

public class AddNote extends AppCompatActivity {

    private NotesDb notesDb;
    private Boolean isInEditMode = false;
    private String _noteId, _title, _text, _color, _state, _lastEdited;

    @BindView(R.id.bottomAppBar) BottomAppBar bottomAppBar;
    @BindView(R.id.btn_done) FloatingActionButton btn_done;
    @BindView(R.id.tv_lastEdited) TextView tv_lastEdited;
    @BindView(R.id.et_title) EditText et_title;
    @BindView(R.id.et_text) EditText et_text;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        ButterKnife.bind(this);
        setSupportActionBar(bottomAppBar);
        notesDb = new NotesDb(this); // Init DB
        new Prefs.Builder().setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE).setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true).build();
        btn_done.setOnClickListener(v -> saveData());

        if (getIntent().getExtras() != null) {
            // Set isInEditMode to true since we are passing data to this intent
            isInEditMode = true;

            // Assign intent extra data to variables
            _noteId = getIntent().getStringExtra("_noteId");
            _title = getIntent().getStringExtra("_title");
            _text = getIntent().getStringExtra("_text");
            _color = getIntent().getStringExtra("_color");
            _state = getIntent().getStringExtra("_state");
            _lastEdited = getIntent().getStringExtra("_lastEdited");

            // Populate stored data in views
            et_title.setText(_title);
            et_text.setText(_text);
            tv_lastEdited.setText(getString(R.string.last_edited) + " : " + _lastEdited);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Disable delete and archive menu options for new notes
        if (!isInEditMode) {
            menu.findItem(R.id.menu_addNote_archive).setVisible(false);
            menu.findItem(R.id.menu_addNote_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem menuItem) {
        return false;
    }

    private void saveData() {
        if (isInEditMode) {
            // Check if something has changed
            if (!getUserInputFromForms()[0].equals(_title)) {
                notesDb.updateItem(_noteId, "title", getUserInputFromForms()[0]);
                finish();
            } else if (!getUserInputFromForms()[1].equals(_text)) {
                notesDb.updateItem(_noteId, "text", getUserInputFromForms()[1]);
                finish();
            }
        } else {
            notesDb.addNote(
                    getUserInputFromForms()[0],
                    getUserInputFromForms()[1],
                    "",
                    "normal",
                    new TextHelpers().getCurrentTimestamp());
            finish();
        }
    }

    private String[] getUserInputFromForms() {
        return new String[]{
                et_title.getText().toString(),
                et_text.getText().toString()
        };
    }
}