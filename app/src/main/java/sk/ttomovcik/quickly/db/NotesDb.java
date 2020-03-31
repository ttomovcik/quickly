package sk.ttomovcik.quickly.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pixplicity.easyprefs.library.Prefs;

import sk.ttomovcik.quickly.helpers.TextHelpers;

import static sk.ttomovcik.quickly.R.color.colorNote_amour;
import static sk.ttomovcik.quickly.R.string.welcomeNote_text;
import static sk.ttomovcik.quickly.R.string.welcomeNote_title;

public class NotesDb extends SQLiteOpenHelper {

    private static final String DB_NAME = "notes.db";
    private static final String DB_TABLE = "notes";
    private static final int DB_VERSION = 1;
    private Context context;

    /**
     * @param context Context
     */
    public NotesDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_TABLE +
                "(id INTEGER PRIMARY KEY," +
                " title TEXT," +
                " text TEXT," +
                " color TEXT," +
                " state TEXT," +
                " lastEdited TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    public void add(String title, String text, String color, String state, String lastEdited) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("text", text);
        contentValues.put("color", color);
        contentValues.put("state", state);
        contentValues.put("lastEdited", lastEdited);
        db.insert(DB_TABLE, null, contentValues);
        db.close();
    }

    @SuppressLint("ResourceType")
    public void createWelcomeNote() {
        add(
                context.getResources().getString(welcomeNote_title),
                context.getResources().getString(welcomeNote_text),
                context.getResources().getString(colorNote_amour),
                "normal",
                new TextHelpers().getCurrentTimestamp()
        );
        Prefs.putBoolean("firstTimeRun", false);
    }

    public void deleteNote(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, "ID=?", new String[]{id});
    }

    public void updateItem(String id, String item, String newValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(item, newValue);
        db.update(DB_TABLE, contentValues, "id = ?", new String[]{id});
    }

    public Cursor getNotes(String filterType, String targetColor) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (filterType) {
            case "all":
                return db.rawQuery("select * from " + DB_TABLE + " order by id asc", null);
            case "allExceptArchived":
                return db.rawQuery("select * from " + DB_TABLE + " where state is not 'archived' order by state asc", null);
            case "favorites":
                return db.rawQuery("select * from " + DB_TABLE + " where state = 'favorite'", null);
            case "archived":
                return db.rawQuery("select * from " + DB_TABLE + " where state = 'archived'", null);
            case "withColor":
                return db.rawQuery("select * from " + DB_TABLE + " where color = \"" + targetColor + "\"", null);
        }
        return null;
    }
}
