package com.example.practicalfive;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.content.res.ColorStateList;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Municipality extends Activity {
    public static final String EXTRA_MUNICIPALITY = "com.example.practicalfive.MUNICIPALITY";
    private String municipalityName;
    private int websiteResource;

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
        Button visitSite = findViewById(R.id.visit_site);
        visitSite.setOnClickListener(view -> openOfficialWebsite());
    }

    private void displayMunicipality() {
        int summaryIndex;
        int imageResource;
        int colourResource;
        switch (municipalityName) {
            case "Beijing":
                summaryIndex = 0;
                imageResource = R.drawable.beijing;
                colourResource = R.color.municipality_beijing;
                websiteResource = R.string.website_beijing;
                break;
            case "Shanghai":
                summaryIndex = 1;
                imageResource = R.drawable.shanghai;
                colourResource = R.color.municipality_shanghai;
                websiteResource = R.string.website_shanghai;
                break;
            case "Chongqing":
                summaryIndex = 2;
                imageResource = R.drawable.chongqing;
                colourResource = R.color.municipality_chongqing;
                websiteResource = R.string.website_chongqing;
                break;
            case "Tianjin":
                summaryIndex = 3;
                imageResource = R.drawable.tianjin;
                colourResource = R.color.municipality_tianjin;
                websiteResource = R.string.website_tianjin;
                break;
            default:
                finish();
                return;
        }
        TextView name = findViewById(R.id.municipality_name);
        TextView information = findViewById(R.id.municipality_information);
        ImageView image = findViewById(R.id.municipality_image);
        int colour = getColor(colourResource);
        int onColour = getColor(municipalityName.equals("Chongqing")
                ? R.color.color_text_dark : R.color.color_text_light);
        name.setBackgroundColor(colour);
        name.setTextColor(onColour);
        information.setTextColor(colour);
        // Bright orange needs a dark surface to keep the required text colour readable.
        information.setBackgroundColor(getColor(municipalityName.equals("Chongqing")
                ? R.color.color_information_background : R.color.color_default_background));
        Button visitSite = findViewById(R.id.visit_site);
        visitSite.setBackgroundTintList(ColorStateList.valueOf(colour));
        visitSite.setTextColor(onColour);
        name.setText(municipalityName);
        information.setText(getResources().getStringArray(R.array.municipalities_array)[summaryIndex]);
        image.setImageResource(imageResource);
        image.setContentDescription(getString(R.string.municipality_image_description, municipalityName));
    }


    private void openOfficialWebsite() {
        Intent websiteIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(websiteResource)));
        try {
            startActivity(websiteIntent);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, R.string.error_no_browser, Toast.LENGTH_LONG).show();
        }
    }
}
