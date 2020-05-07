package ch.epfl.polycrowd;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditTextActivity extends AppCompatActivity {

    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        text = findViewById(R.id.editable_text);
//        text.setBackgroundColor(Color.TRANSPARENT);
    }

    public void onEditClicked(View view) {
        text.setFocusableInTouchMode(false);
        text.setEnabled(false);
        text.setCursorVisible(false);
//        text.setKeyListener(null);
    }

    public void onEditOnCicked(View view) {
        text.setFocusableInTouchMode(true);
        text.setEnabled(true);
        text.setCursorVisible(true);
//        text.setKeyListener();
//        text.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
    }
}
