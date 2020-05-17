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
import androidx.appcompat.app.AppCompatDelegate;
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
        intentAddNote = new Intent(this, AddNote.class);
        intentAddNote.putExtra("_action", "0");
        startActivity(intentAddNote);
    }

    private final static String TAG = "MainActivity";
    private List<Note> noteArrayList = new ArrayList<>();
    private Boolean isNoteViewPrepared = false;
    private NotesAdapter notesAdapter;
    private String rDataAction, rDataType;
    private NotesDb notesDb;
    private Intent intentReceivedData, intentAddNote;

    @SuppressLint("ResourceType") @Override
    protected void onCreate(Bundle savedInstanceState) {
        intentReceivedData = getIntent();
        rDataAction = intentReceivedData.getAction();
        rDataType = intentReceivedData.getType();

        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        ButterKnife.bind(this);
        new Prefs.Builder().setContext(this).setMode(ContextWrapper.MODE_PRIVATE).setPrefsName(getPackageName()).setUseDefaultSharedPreference(true).build();
        notesDb = new NotesDb(this);
        notesAdapter = new NotesAdapter(this, noteArrayList);
        prepareAppWithStoredSettings();
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setNavigationOnClickListener(v -> BottomModalSheetNavigation.newInstance().show(this.getSupportFragmentManager(), "ah_sheet"));
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        showFilterNotification(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            Intent i = new Intent(this, AddNote.class);
            i.putExtra("_action", "1");
            i.putExtra("_text", sharedText);
            startActivity(i);
        }
    }

    private void prepareAppWithStoredSettings() {
        switch (Integer.parseInt(Prefs.getString("appTheme", "0"))) {
            case 0: // Default (auto)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1: // Light
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 2: // Dark
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }

        if (Prefs.getBoolean("firstTimeRun", true)) {
            notesDb.createWelcomeNote();
        }

        if (Prefs.getString("fabPosition", "end").contains("center")) {
            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        }

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
