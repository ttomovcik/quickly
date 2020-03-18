package sk.ttomovcik.quickly.model;

public class Note {

    private String title, text, state;
    private int _Id;

    public Note() {
    }

    public Note(int _Id, String title, String text, String state) {
        this._Id = _Id;
        this.title = title;
        this.text = text;
        this.state = state;
    }

    public int get_Id() {
        return _Id;
    }

    public void set_Id(int _Id) {
        this._Id = _Id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle() {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText() {
        this.text = text;
    }

    public String getState() {
        return state;
    }

    public void setState() {
        this.state = state;
    }
}
