package sk.ttomovcik.quickly.activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.db.NotesDb;
import sk.ttomovcik.quickly.helpers.TextHelpers;

import static java.util.Objects.requireNonNull;
import static sk.ttomovcik.quickly.R.color.colorNote_amour;
import static sk.ttomovcik.quickly.R.color.colorNote_cyanite;
import static sk.ttomovcik.quickly.R.color.colorNote_lotusPink;
import static sk.ttomovcik.quickly.R.color.colorNote_mountainMeadow;
import static sk.ttomovcik.quickly.R.color.colorNote_reallyOrange;
import static sk.ttomovcik.quickly.R.drawable.ic_delete_forever_24dp;
import static sk.ttomovcik.quickly.R.drawable.ic_favorite_24dp;
import static sk.ttomovcik.quickly.R.drawable.ic_favorite_border_24dp;
import static sk.ttomovcik.quickly.R.id.menu_addNote_addToFavorites;
import static sk.ttomovcik.quickly.R.id.menu_addNote_archive;
import static sk.ttomovcik.quickly.R.id.menu_addNote_color;
import static sk.ttomovcik.quickly.R.id.menu_addNote_delete;
import static sk.ttomovcik.quickly.R.id.menu_addNote_share;
import static sk.ttomovcik.quickly.R.layout.activity_add_note;
import static sk.ttomovcik.quickly.R.menu.menu_add_note;
import static sk.ttomovcik.quickly.R.string.added_to_favorites;
import static sk.ttomovcik.quickly.R.string.app_signature;
import static sk.ttomovcik.quickly.R.string.app_store_link;
import static sk.ttomovcik.quickly.R.string.choose_color;
import static sk.ttomovcik.quickly.R.string.colorNoteName_amour;
import static sk.ttomovcik.quickly.R.string.colorNoteName_cyanite;
import static sk.ttomovcik.quickly.R.string.colorNoteName_lotusPink;
import static sk.ttomovcik.quickly.R.string.colorNoteName_mountainMeadow;
import static sk.ttomovcik.quickly.R.string.colorNoteName_reallyOrange;
import static sk.ttomovcik.quickly.R.string.colorNoteName_removeColor;
import static sk.ttomovcik.quickly.R.string.exiting_on_unsaved_note;
import static sk.ttomovcik.quickly.R.string.last_edited;
import static sk.ttomovcik.quickly.R.string.no;
import static sk.ttomovcik.quickly.R.string.remove_from_favorites;
import static sk.ttomovcik.quickly.R.string.removed_from_favorites;
import static sk.ttomovcik.quickly.R.string.right_now;
import static sk.ttomovcik.quickly.R.string.share_note;
import static sk.ttomovcik.quickly.R.string.unsaved_changes;
import static sk.ttomovcik.quickly.R.string.untitled_note;
import static sk.ttomovcik.quickly.R.string.yes;

public class AddNote extends AppCompatActivity {

    private Intent share;
    private String[] colors, userInput;
    private NotesDb notesDb;
    private Boolean isInEditMode = false;
    private Boolean hasFavColorChanged = false;
    private TextHelpers helper;
    private String _noteId, _title, _text, _color, _state, _lastEdited, _pickedColor;

    @BindView(R.id.bottomAppBar) BottomAppBar bottomAppBar;
    @BindView(R.id.btn_done) FloatingActionButton btn_done;
    @BindView(R.id.noteColor) FloatingActionButton noteColor;
    @BindView(R.id.tv_lastEdited) TextView tv_lastEdited;
    @BindView(R.id.et_title) EditText et_title;
    @BindView(R.id.et_text) EditText et_text;

    @SuppressLint("SetTextI18n") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_add_note);
        ButterKnife.bind(this);
        setSupportActionBar(bottomAppBar);
        notesDb = new NotesDb(this);
        helper = new TextHelpers();
        new Prefs.Builder().setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE).setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true).build();
        btn_done.setOnClickListener(v -> saveData());
        if (Prefs.getString("fabPosition", "end").contains("center")) {
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        }

        prepareActivityForEditingNote();
        colors = new String[]{
                getString(colorNoteName_amour),
                getString(colorNoteName_mountainMeadow),
                getString(colorNoteName_cyanite),
                getString(colorNoteName_reallyOrange),
                getString(colorNoteName_lotusPink),
                getString(colorNoteName_removeColor)
        };
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu_add_note, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }
        if (Objects.equals(_state, "favorite")) {
            menu.findItem(menu_addNote_addToFavorites).setIcon(ic_favorite_24dp);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isInEditMode) {
            menu.findItem(menu_addNote_archive).setVisible(false);
            menu.findItem(menu_addNote_delete).setVisible(false);
            menu.findItem(menu_addNote_share).setVisible(false);
            menu.findItem(menu_addNote_addToFavorites).setVisible(false);
        }
        if (Prefs.getBoolean("disableShareButton", false)) {
            menu.findItem(menu_addNote_share).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case menu_addNote_color:
                showColorPicker();
                return true;
            case menu_addNote_addToFavorites:
                if (_state.contains("favorite")) {
                    notesDb.updateItem(_noteId, "state", "normal");
                    _state = "normal";
                    menuItem.setIcon(ic_favorite_border_24dp);
                    Toast.makeText(this, getString(removed_from_favorites), Toast.LENGTH_SHORT).show();
                } else {
                    notesDb.updateItem(_noteId, "state", "favorite");
                    _state = "favorite";
                    menuItem.setIcon(ic_favorite_24dp);
                    menuItem.setTitle(remove_from_favorites);
                    Toast.makeText(this, getString(added_to_favorites), Toast.LENGTH_SHORT).show();
                }
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
                    share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, Prefs.getBoolean("enableShareSignature", false)
                            ? _title + ": " + _text + "\n" + getString(app_signature) + " " + getString(app_store_link)
                            : _title + ": " + _text);
                    startActivity(Intent.createChooser(share, getString(share_note)));
                }
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (hasTextChanged()) notifyOnExit();
        else finish();
    }


    @SuppressLint("SetTextI18n")
    private void prepareActivityForEditingNote() {
        int actionType = Integer.parseInt(requireNonNull(getIntent().getStringExtra("_action")));

        switch (actionType) {
            case 0:
                // Default action, new note
                tv_lastEdited.setText(getString(last_edited) + " " + getString(right_now));
                break;
            case 1:
                // Editing existing task
                isInEditMode = true;

                _noteId = getIntent().getStringExtra("_noteId");
                _title = getIntent().getStringExtra("_title");
                _text = getIntent().getStringExtra("_text");
                _color = getIntent().getStringExtra("_color");
                _state = getIntent().getStringExtra("_state");
                _lastEdited = getIntent().getStringExtra("_lastEdited");

                et_title.setText(_title);
                et_text.setText(_text);
                tv_lastEdited.setText(getString(last_edited) + " " + _lastEdited);


                if (!helper.isEmpty(_color)) {
                    noteColor.setVisibility(View.VISIBLE);
                    noteColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(_color)));
                }
                break;
            case 2:
                // Received data from other activity
                _lastEdited = (getString(last_edited) + ": " + getString(right_now));
                tv_lastEdited.setText(getString(last_edited) + " : " + _lastEdited);
                et_text.setText("received data");
                break;
        }
    }

    @SuppressLint("ResourceType")
    private void showColorPicker() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(choose_color))
                .setItems(colors, (dialog, which) -> {
                    switch (which) {
                        case 0: // Red
                            hasFavColorChanged = true;
                            _pickedColor = getString(colorNote_amour);
                            break;
                        case 1: // Green
                            hasFavColorChanged = true;
                            _pickedColor = getString(colorNote_mountainMeadow);
                            break;
                        case 2: // Blue
                            hasFavColorChanged = true;
                            _pickedColor = getString(colorNote_cyanite);
                            break;
                        case 3: // Orange
                            hasFavColorChanged = true;
                            _pickedColor = getString(colorNote_reallyOrange);
                            break;
                        case 4: // Pink
                            hasFavColorChanged = true;
                            _pickedColor = getString(colorNote_lotusPink);
                            break;
                        case 5:
                            hasFavColorChanged = true;
                            _pickedColor = null;
                            break;
                        default:

                            break;
                    }
                }).create().show();
    }

    private Boolean hasTextChanged() {
        userInput = new String[]{et_title.getText().toString(), et_text.getText().toString()};
        if (isInEditMode) {
            return !Objects.equals(_title, userInput[0]) || !Objects.equals(_text, userInput[1]);
        } else return !Objects.equals(_title, userInput[0]) || !Objects.equals(_text, userInput[1]);
    }

    private void notifyOnExit() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(unsaved_changes))
                .setMessage(getString(exiting_on_unsaved_note))
                .setIcon(getDrawable(ic_delete_forever_24dp))
                .setPositiveButton(getString(yes), (dialog, which) -> finish())
                .setNegativeButton(getString(no), null)
                .create().show();
    }

    private void saveData() {
        userInput = new String[]{et_title.getText().toString(), et_text.getText().toString()};
        if (isInEditMode) {
            if (!userInput[0].equals(_title)) {
                notesDb.updateItem(_noteId, "title", userInput[0]);
                notesDb.updateItem(_noteId, "lastEdited", helper.getCurrentTimestamp());
            } else if (!userInput[1].equals(_text)) {
                notesDb.updateItem(_noteId, "text", userInput[1]);
                notesDb.updateItem(_noteId, "lastEdited", helper.getCurrentTimestamp());
            }
        } else {
            if (helper.isEmpty(userInput[0])) {
                notesDb.add(getString(untitled_note), userInput[1], _pickedColor,
                        "normal", helper.getCurrentTimestamp());
            } else if (helper.isEmpty(userInput[1])) {
                notesDb.add(userInput[0], "", _pickedColor, "normal", helper.getCurrentTimestamp());
            } else {
                notesDb.add(userInput[0], userInput[1], _pickedColor, "normal", helper.getCurrentTimestamp());
            }
        }
        if (hasFavColorChanged) notesDb.updateItem(_noteId, "color", _pickedColor);
        finish();
    }
}