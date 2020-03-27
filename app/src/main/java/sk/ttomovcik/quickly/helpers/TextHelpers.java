package sk.ttomovcik.quickly.helpers;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TextHelpers {

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public String getCurrentTimestamp() {
        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        return df.format(Calendar.getInstance().getTime());
    }
}
