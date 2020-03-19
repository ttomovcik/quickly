package sk.ttomovcik.quickly;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sk.ttomovcik.quickly.adapters.NotesAdapter;
import sk.ttomovcik.quickly.db.NotesDb;
import sk.ttomovcik.quickly.helpers.SwipeToArchiveCallback;
import sk.ttomovcik.quickly.model.Note;
import sk.ttomovcik.quickly.views.AddNoteBottomModalSheet;
import sk.ttomovcik.quickly.views.BottomModalSheetNavigation;

public class MainActivity extends AppCompatActivity {

    private List<Note> noteList = new ArrayList<>();
    private NotesAdapter notesAdapter = new NotesAdapter(noteList, this);

    NotesDb notesDb;
    Boolean isFilterActive = false;

    @BindView(R.id.bottomAppBar)
    BottomAppBar bottomAppBar;
    @BindView(R.id.rv_notes)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tv_appTitle)
    TextView tv_appTitle;

    @OnClick(R.id.fab)
    void createNote() {
        openBottomModalSheet("addNote");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(bottomAppBar);
        notesDb = new NotesDb(this);
        bottomAppBar.setNavigationOnClickListener(v -> openBottomModalSheet("navigation"));
        prepareRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        notesDb.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bottomappbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_archivedItems:
                if (!isFilterActive) {
                    loadRecyclerView("archived");
                    isFilterActive = true;
                    tv_appTitle.setText(getString(R.string.title_archivedItems));
                } else {
                    loadRecyclerView("allExceptArchived");
                    isFilterActive = false;
                    tv_appTitle.setText(getString(R.string.title_my_notes));
                }
                return true;
            case R.id.menu_favorites:
                if (!isFilterActive) {
                    loadRecyclerView("favorites");
                    isFilterActive = true;
                    tv_appTitle.setText(getString(R.string.title_favorites));
                } else {
                    loadRecyclerView("allExceptArchived");
                    isFilterActive = false;
                    tv_appTitle.setText(getString(R.string.title_my_notes));
                }
                return true;
        }
        return false;
    }

    private void openBottomModalSheet(String target) {
        switch (target) {
            case "navigation":
                BottomModalSheetNavigation navigation = BottomModalSheetNavigation.newInstance();
                navigation.show(getSupportFragmentManager(), "BottomSheetnavigation");
                break;
            case "addNote":
                AddNoteBottomModalSheet addNote = AddNoteBottomModalSheet.newInstance();
                addNote.show(getSupportFragmentManager(), "BottomSheetnavigation");
                break;
        }
    }

    public void prepareRecyclerView() {
        tv_appTitle.setText(getString(R.string.title_my_notes));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(notesAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToArchiveCallback(notesAdapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        loadRecyclerView("allExceptArchived");
    }

    public void loadRecyclerView(String notesCategory) {
        Cursor dataFromDb = notesDb.getNotes(notesCategory);
        noteList.clear();
        if (dataFromDb != null) {
            dataFromDb.moveToFirst();
            while (!dataFromDb.isAfterLast()) {
                Note note = new Note(
                        dataFromDb.getInt(0),
                        dataFromDb.getString(1),
                        dataFromDb.getString(2),
                        dataFromDb.getString(3));
                noteList.add(note);
                dataFromDb.moveToNext();
            }
        }
        notesAdapter.notifyDataSetChanged();
    }

    public void openNoteInEditMode(String id, String title, String text) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("setEditMode", true);
        bundle.putString("id", id);
        bundle.putString("title", title);
        bundle.putString("text", text);
        AddNoteBottomModalSheet addNote = AddNoteBottomModalSheet.newInstance();
        addNote.setArguments(bundle);
        addNote.show(getSupportFragmentManager(), "BottomSheetnavigation");
    }
}
