package sk.ttomovcik.quickly.activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.db.NotesDb;
import sk.ttomovcik.quickly.helpers.TextHelpers;

import static sk.ttomovcik.quickly.R.id.menu_addNote_archive;
import static sk.ttomovcik.quickly.R.id.menu_addNote_color;
import static sk.ttomovcik.quickly.R.id.menu_addNote_delete;
import static sk.ttomovcik.quickly.R.id.menu_addNote_share;
import static sk.ttomovcik.quickly.R.layout.activity_add_note;
import static sk.ttomovcik.quickly.R.menu.menu_add_note;
import static sk.ttomovcik.quickly.R.string;

public class AddNote extends AppCompatActivity {

    private String[] colors;
    private NotesDb notesDb;
    private Boolean isInEditMode = false;
    private Boolean hasFavColorChanged = false;
    private TextHelpers textHelpers;
    private String _noteId, _title, _text, _color, _state, _lastEdited, _colorFromPicker;

    @BindView(R.id.bottomAppBar) BottomAppBar bottomAppBar;
    @BindView(R.id.btn_done) FloatingActionButton btn_done;
    @BindView(R.id.noteColor) FloatingActionButton noteColor;
    @BindView(R.id.tv_lastEdited) TextView tv_lastEdited;
    @BindView(R.id.et_title) EditText et_title;
    @BindView(R.id.et_text) EditText et_text;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_add_note);
        ButterKnife.bind(this);
        setSupportActionBar(bottomAppBar);
        notesDb = new NotesDb(this); // Init DB
        textHelpers = new TextHelpers();
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
            tv_lastEdited.setText(getString(string.last_edited) + " : " + _lastEdited);

            if (!textHelpers.isEmpty(_color)) {
                noteColor.setVisibility(View.VISIBLE);
                noteColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(_color)));
            }
        }
        colors = new String[]{
                getResources().getString(R.string.colorNote_amour),
                getResources().getString(R.string.colorNote_mountainMeadow),
                getResources().getString(R.string.colorNote_cyanite),
                getResources().getString(R.string.colorNote_reallyOrange),
                getResources().getString(R.string.colorNote_lotusPink)
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!isInEditMode) {
            menu.findItem(menu_addNote_archive).setVisible(false);
            menu.findItem(menu_addNote_delete).setVisible(false);
            menu.findItem(menu_addNote_share).setVisible(false);
        }
        return true;
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case menu_addNote_color:
                new MaterialAlertDialogBuilder(this)
                        .setTitle(getResources().getString(string.title_choose_color))
                        .setItems(colors, (dialog, which) -> {
                            hasFavColorChanged = true;
                            switch (which) {
                                case 0: // Red
                                    _colorFromPicker = getResources().getString(R.color.colorNote_amour);
                                    break;
                                case 1: // Green
                                    _colorFromPicker = getResources().getString(R.color.colorNote_mountainMeadow);
                                    break;
                                case 2: // Blue
                                    _colorFromPicker = getResources().getString(R.color.colorNote_cyanite);
                                    break;
                                case 3: // Orange
                                    _colorFromPicker = getResources().getString(R.color.colorNote_reallyOrange);
                                    break;
                                case 4: // Pink
                                    _colorFromPicker = getResources().getString(R.color.colorNote_lotusPink);
                                    break;
                            }
                        }).create().show();
                return true;
            case menu_addNote_archive:
                notesDb.updateItem(_noteId, "state", "archived");
                finish();
                return true;
            case menu_addNote_delete:
                notesDb.deleteNote(_noteId);
                finish();
                return true;
            case menu_addNote_share:
                if (isInEditMode) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, _title + ": " + _text);
                    startActivity(Intent.createChooser(share, getResources().getString(string.share)));
                }
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isInEditMode) {
            // Compare extras from bundle to userInput
            if (!_title.equals(getTextInput()[0]) || !_text.equals(getTextInput()[1])) {
                Log.d("asdf", "something has changed");
            }
        } else {
            // Normal new note, display exit notification
            if (!textHelpers.isEmpty(getTextInput()[0]) || !textHelpers.isEmpty(getTextInput()[1])) {
                Log.d("asdf", "sdadsdasdsdasdasdasdasds");
            }
        }
    }

    private void saveData() {
        if (isInEditMode) {
            // Check if something has changed
            if (!getTextInput()[0].equals(_title)) {
                notesDb.updateItem(_noteId, "title", getTextInput()[0]);
                notesDb.updateItem(_noteId, "lastEdited", textHelpers.getCurrentTimestamp());
            } else if (!getTextInput()[1].equals(_text)) {
                notesDb.updateItem(_noteId, "text", getTextInput()[1]);
                notesDb.updateItem(_noteId, "lastEdited", textHelpers.getCurrentTimestamp());
            }
        } else {
            notesDb.addNote(getTextInput()[0], getTextInput()[1],
                    "",
                    "normal",
                    textHelpers.getCurrentTimestamp());
        }
        if (hasFavColorChanged) notesDb.updateItem(_noteId, "color", _colorFromPicker);
        finish();
    }

    private String[] getTextInput() {
        return new String[]{
                et_title.getText().toString(),
                et_text.getText().toString()
        };
    }
}