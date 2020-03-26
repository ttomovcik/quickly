package sk.ttomovcik.quickly.helpers;

public class TextHelpers {

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }
}
