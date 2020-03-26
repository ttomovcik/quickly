package sk.ttomovcik.quickly.activities;

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

public class AddNote extends AppCompatActivity {

    private NotesDb notesDb;
    private Boolean isInEditMode = false;
    private String _id, _title, _text, _color, _state;

    @BindView(R.id.bottomAppBar) BottomAppBar bottomAppBar;
    @BindView(R.id.btn_done) FloatingActionButton btn_done;
    @BindView(R.id.tv_lastEdited) TextView tv_lastEdited;
    @BindView(R.id.et_title) EditText et_title;
    @BindView(R.id.et_text) EditText et_text;

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

        if (getIntent().getExtras() != null) {
            // Set isInEditMode to true since we are passing data to this intent
            isInEditMode = true;

            // Assign intent extra data to variables
            _id = getIntent().getStringExtra("_id");
            _title = getIntent().getStringExtra("_title");
            _text = getIntent().getStringExtra("_text");
            _color = getIntent().getStringExtra("_color");
            _state = getIntent().getStringExtra("_state");

            // Populate stored data in views
            et_title.setText(_title);
            et_text.setText(_text);
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
}