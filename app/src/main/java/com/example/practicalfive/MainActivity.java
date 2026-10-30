package com.example.practicalfive;

import android.app.Activity;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class MainActivity extends Activity {
    private final TextProcessor processor = new TextProcessor();
    private EditText municipalityInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScreenInsets.apply(this, findViewById(R.id.main_root));
        municipalityInput = findViewById(R.id.municipality_input);
        municipalityInput.setOnEditorActionListener((view, actionId, event) -> {
            boolean enterPressed = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            if (enterPressed) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                    submitMunicipality();
                }
                return true;
            }
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                submitMunicipality();
                return true;
            }
            return false;
        });
    }

    private void submitMunicipality() {
        String input = municipalityInput.getText().toString();
        String name = processor.processMunicipality(input);
        if (name == null) {
            municipalityInput.setError(getString(input.trim().isEmpty()
                    ? R.string.error_empty : R.string.error_unsupported));
            return;
        }
        municipalityInput.setError(null);
        municipalityInput.setText(name);
        InputMethodManager keyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (keyboard != null) {
            keyboard.hideSoftInputFromWindow(municipalityInput.getWindowToken(), 0);
        }
        Intent intent = new Intent(this, Municipality.class);
        intent.putExtra(Municipality.EXTRA_MUNICIPALITY, name);
        startActivity(intent);
    }
}
