package com.example.practicalfive;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Municipality extends Activity {
    public static final String EXTRA_MUNICIPALITY = "com.example.practicalfive.MUNICIPALITY";
    private String municipalityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        municipalityName = new TextProcessor().processMunicipality(
                getIntent().getStringExtra(EXTRA_MUNICIPALITY));
        if (municipalityName == null) {
            Toast.makeText(this, R.string.error_missing_municipality, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setContentView(R.layout.activity_municipality);
        ScreenInsets.apply(this, findViewById(R.id.municipality_root));
        displayMunicipality();
    }

    private void displayMunicipality() {
        int summaryIndex;
        int imageResource;
        switch (municipalityName) {
            case "Beijing":
                summaryIndex = 0;
                imageResource = R.drawable.beijing;
                break;
            case "Shanghai":
                summaryIndex = 1;
                imageResource = R.drawable.shanghai;
                break;
            case "Chongqing":
                summaryIndex = 2;
                imageResource = R.drawable.chongqing;
                break;
            case "Tianjin":
                summaryIndex = 3;
                imageResource = R.drawable.tianjin;
                break;
            default:
                finish();
                return;
        }
        TextView name = findViewById(R.id.municipality_name);
        TextView information = findViewById(R.id.municipality_information);
        ImageView image = findViewById(R.id.municipality_image);
        name.setText(municipalityName);
        information.setText(getResources().getStringArray(R.array.municipalities_array)[summaryIndex]);
        image.setImageResource(imageResource);
        image.setContentDescription(getString(R.string.municipality_image_description, municipalityName));
    }
}
