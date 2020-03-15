package sk.ttomovcik.quickly.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.views.BottomModalSheetNavigation;

public class MainActivity extends AppCompatActivity {

    BottomAppBar bottomAppBar;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setNavigationOnClickListener(v -> openNavigationModalSheet());
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
                return true;
            case R.id.menu_favorites:
                return true;

        }
        return false;
    }

    /**
     * Opens up a bottomNavigationSheet with menu and profile info
     */
    private void openNavigationModalSheet() {
        BottomModalSheetNavigation navigation = BottomModalSheetNavigation.newInstance();
        navigation.show(getSupportFragmentManager(), "BottomSheetnavigation");
    }
}
