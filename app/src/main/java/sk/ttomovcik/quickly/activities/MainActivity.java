package sk.ttomovcik.quickly.activities;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

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
import sk.ttomovcik.quickly.views.BottomModalSheetFilter;
import sk.ttomovcik.quickly.views.BottomModalSheetNavigation;

import static sk.ttomovcik.quickly.R.anim.layout_anim_fall_down;
import static sk.ttomovcik.quickly.R.id.menu_main_filter;
import static sk.ttomovcik.quickly.R.layout.activity_main;
import static sk.ttomovcik.quickly.R.menu.menu_main;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomAppBar) BottomAppBar bottomAppBar;
    @BindView(R.id.rv_noteView) RecyclerView recyclerView;
    @BindView(R.id.filterNotification) LinearLayout filterNotification;

    @OnClick(R.id.btn_createNote) void createNote() {
        startActivity(new Intent(this, AddNote.class));
    }

    private final static String TAG = "MainActivity";
    private List<Note> noteArrayList = new ArrayList<>();
    private Boolean isNoteViewPrepared = false;
    private NotesAdapter notesAdapter;
    private NotesDb notesDb;

    @SuppressLint("ResourceType") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        ButterKnife.bind(this);
        notesDb = new NotesDb(this);
        notesAdapter = new NotesAdapter(this, noteArrayList);
        new Prefs.Builder().setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE).setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true).build();

        setSupportActionBar(bottomAppBar);
        bottomAppBar.setNavigationOnClickListener(v -> BottomModalSheetNavigation.newInstance()
                .show(this.getSupportFragmentManager(), "ah_sheet"));

        if (Prefs.getBoolean("firstTimeRun", true)) notesDb.createWelcomeNote();
    }

    @Override
    protected void onPause() {
        super.onPause();
        notesDb.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> loadRecyclerView("allExceptArchived", ""), 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notesDb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem menuItem) {
        if (menuItem.getItemId() == menu_main_filter) {
            BottomModalSheetFilter.newInstance().show(this.getSupportFragmentManager(), "ah_sheet");
        }
        return false;
    }

    public void loadRecyclerView(String filter, String color) {
        if (isNoteViewPrepared) {
            Log.i(TAG, "Reloading recyclerView");
            recyclerView.setLayoutManager(null);
            recyclerView.setAdapter(null);
        }
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, layout_anim_fall_down);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutAnimation(animation);
        recyclerView.setAdapter(notesAdapter);
        try (Cursor c = notesDb.getNotes(filter, color)) {
            noteArrayList.clear();
            c.moveToFirst();
            while (!c.isAfterLast()) {
                Note note = new Note(
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5));
                noteArrayList.add(note);
                c.moveToNext();
            }
        }
        isNoteViewPrepared = true;
        notesAdapter.notifyDataSetChanged();
    }

    public void showFilterNotification(Boolean state) {
        if (state) {
            filterNotification.setOnClickListener(v -> {
                filterNotification.setVisibility(View.GONE);
                loadRecyclerView("allExceptArchived", "");
            });
        }
        filterNotification.setVisibility(state ? View.VISIBLE : View.GONE);
    }
}
