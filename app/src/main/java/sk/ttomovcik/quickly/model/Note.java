package sk.ttomovcik.quickly.model;

public class Note {

    private String title, text, state, color, lastEdited;
    private int _Id;

    public Note() {
    }

    public Note(int _Id, String title,
                String text, String color,
                String state, String lastEdited) {
        this._Id = _Id;
        this.title = title;
        this.text = text;
        this.color = color;
        this.state = state;
        this.lastEdited = lastEdited;
    }

    // ID
    public int get_Id() {
        return _Id;
    }

    public void set_Id(int _Id) {
        this._Id = _Id;
    }


    // Title
    public String getTitle() {
        return title;
    }

    public void setTitle() {
        this.title = title;
    }


    // Text
    public String getText() {
        return text;
    }

    public void setText() {
        this.text = text;
    }


    // Color
    public String getColor() {
        return color;
    }

    public void setColor() {
        this.color = color;
    }


    // State
    public String getState() {
        return state;
    }

    public void setState() {
        this.state = state;
    }
    
    // Last edited

    public String getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(String lastEdited) {
        this.lastEdited = lastEdited;
    }
}
