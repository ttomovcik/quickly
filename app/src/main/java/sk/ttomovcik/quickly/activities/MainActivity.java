package sk.ttomovcik.quickly.activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import sk.ttomovcik.quickly.helpers.TextHelpers;
import sk.ttomovcik.quickly.model.Note;
import sk.ttomovcik.quickly.views.BottomModalSheetNavigation;

/*
 * Here, track my progress I will
 *
 * - Moved FloatinActionButton in BottomAppBar to end
 * - New UI for adding and editing notes
 * - Added option to add color to notes
 * - Faster app startup
 */

/*
 * TODOS:
 *
 * TODO: Add setting to change FAB position
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
        Log.d("WTF", "THIS SHIT WORKS");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        new Prefs.Builder().setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE).setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true).build();
        notesAdapter = new NotesAdapter(this, noteList);
        notesDb = new NotesDb(this);
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setNavigationOnClickListener(v -> BottomModalSheetNavigation.newInstance()
                .show(MainActivity.this.getSupportFragmentManager(), "BottomSheetnavigation"));

        if (Prefs.getBoolean("firstTimeRun", true)) {
            notesDb.addNote(
                    "Welcome to Quickly",
                    "Do whatever the fuck you want, if you break this, cry",
                    getResources().getString(R.color.colorNote_amour),
                    "normal",
                    new TextHelpers().getCurrentTimestamp());
            Prefs.putBoolean("firstTimeRun", false);
        }
        Log.d("currentDateTime", new TextHelpers().getCurrentTimestamp());
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        Cursor dataFromDb = notesDb.getNotes(filter, "*");
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
        notesAdapter.notifyDataSetChanged();
    }
}
