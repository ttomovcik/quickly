package sk.ttomovcik.quickly;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import sk.ttomovcik.quickly.R;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        finish();
    }
}
