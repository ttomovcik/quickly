package sk.ttomovcik.quickly.activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.adapters.NotesAdapter;
import sk.ttomovcik.quickly.db.NotesDb;
import sk.ttomovcik.quickly.model.Note;
import sk.ttomovcik.quickly.views.BottomModalSheetNavigation;

import static sk.ttomovcik.quickly.R.layout.activity_main;
import static sk.ttomovcik.quickly.R.menu.menu_main;

/*
 * Here, track my progress I will.
 *
 * - Moved FloatinActionButton in BottomAppBar to end
 * - New UI for adding and editing notes
 * - Added option to add color to notes
 * - Faster app startup (on some devices)
 * - Added warning before exiting in AddNote activity
 * - Temporary removed sk-SK translation
 * - Note title and text now show 3 dots instead of filling entire screen
 * - Added option to share selected note
 * - Welcome note will be created by default if app is running for the first time
 */

/*
 *  TODOS:
 *
 *  TODO: Add setting to change FAB position
 *  TODO: Add option to include app signature in send Intent
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomAppBar) BottomAppBar bottomAppBar;
    @BindView(R.id.rv_noteView) RecyclerView rv_noteView;

    @OnClick(R.id.btn_createNote) void createNote() {
        startActivity(new Intent(this, AddNote.class));
    }

    private List<Note> noteList = new ArrayList<>();
    private Boolean isNoteViewPrepared = false;
    private NotesAdapter notesAdapter;
    private NotesDb notesDb;

    @SuppressLint("ResourceType") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        ButterKnife.bind(this);
        notesDb = new NotesDb(this);
        notesAdapter = new NotesAdapter(this, noteList);

        setSupportActionBar(bottomAppBar);
        bottomAppBar.setNavigationOnClickListener(v -> BottomModalSheetNavigation.newInstance()
                .show(MainActivity.this.getSupportFragmentManager(), "BottomSheetnavigation"));

        new Thread(() -> {
            new Prefs.Builder().setContext(this)
                    .setMode(ContextWrapper.MODE_PRIVATE).setPrefsName(getPackageName())
                    .setUseDefaultSharedPreference(true).build();
            if (Prefs.getBoolean("firstTimeRun", true)) {
                notesDb.createWelcomeNote();
                Prefs.putBoolean("firstTimeRun", false);
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        notesDb.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecyclerView("allExceptArchived");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem menuItem) {
        return false;
    }

    public void loadRecyclerView(String filter) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        // Recreate RecyclerView if called from somewhere else
        if (isNoteViewPrepared) {
            rv_noteView.setLayoutManager(null);
            rv_noteView.setAdapter(null);
        }
        rv_noteView.setLayoutManager(layoutManager);
        rv_noteView.setItemAnimator(new DefaultItemAnimator());
        rv_noteView.setAdapter(notesAdapter);
        try (Cursor dataFromDb = notesDb.getNotes(filter, "*")) {
            noteList.clear();
            if (dataFromDb != null) {
                dataFromDb.moveToFirst();
                while (!dataFromDb.isAfterLast()) {
                    Note note = new Note(
                            dataFromDb.getInt(0),
                            dataFromDb.getString(1),
                            dataFromDb.getString(2),
                            dataFromDb.getString(3),
                            dataFromDb.getString(4),
                            dataFromDb.getString(5));
                    noteList.add(note);
                    dataFromDb.moveToNext();
                }
            }
        }
        notesAdapter.notifyDataSetChanged();
    }
}
