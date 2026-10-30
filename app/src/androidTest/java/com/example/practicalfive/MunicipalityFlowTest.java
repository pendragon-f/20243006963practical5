package com.example.practicalfive;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MunicipalityFlowTest {
    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

    @Test public void keyboardNavigationDisplaysAllFourMunicipalities() throws Exception {
        String[] inputs = {"BEIJING", "  shanghai  ", "ChOnGqInG", "tianjin"};
        String[] names = {"Beijing", "Shanghai", "Chongqing", "Tianjin"};
        int[] colours = {R.color.municipality_beijing, R.color.municipality_shanghai,
                R.color.municipality_chongqing, R.color.municipality_tianjin};
        int[] pictures = {R.drawable.beijing, R.drawable.shanghai,
                R.drawable.chongqing, R.drawable.tianjin};
        String[] urls = {"https://english.beijing.gov.cn/", "https://english.shanghai.gov.cn/",
                "https://english.cq.gov.cn/", "http://en.tj.gov.cn/"};

        for (int i = 0; i < names.length; i++) {
            final int index = i;
            Instrumentation.ActivityMonitor detailMonitor = instrumentation.addMonitor(
                    Municipality.class.getName(), null, false);
            try (ActivityScenario<MainActivity> main = ActivityScenario.launch(MainActivity.class)) {
                if (i == 0) saveScreenshot("main");
                main.onActivity(activity -> {
                    EditText input = activity.findViewById(R.id.municipality_input);
                    input.setText(inputs[index]);
                    input.onEditorAction(EditorInfo.IME_ACTION_GO);
                });
                Activity detail = instrumentation.waitForMonitorWithTimeout(detailMonitor, 5000);
                assertNotNull("An explicit intent should open Municipality", detail);
                instrumentation.waitForIdleSync();
                instrumentation.runOnMainSync(() -> {
                    TextView name = detail.findViewById(R.id.municipality_name);
                    TextView information = detail.findViewById(R.id.municipality_information);
                    ImageView image = detail.findViewById(R.id.municipality_image);
                    Button button = detail.findViewById(R.id.visit_site);
                    assertEquals(names[index], name.getText().toString());
                    assertEquals(detail.getResources().getStringArray(R.array.municipalities_array)[index],
                            information.getText().toString());
                    assertEquals(detail.getColor(colours[index]), information.getCurrentTextColor());
                    assertEquals(detail.getColor(colours[index]),
                            ((ColorDrawable) name.getBackground()).getColor());
                    assertEquals(detail.getColor(colours[index]),
                            button.getBackgroundTintList().getDefaultColor());
                    assertTrue(((BitmapDrawable) image.getDrawable()).getBitmap().sameAs(
                            ((BitmapDrawable) detail.getDrawable(pictures[index])).getBitmap()));
                    assertTrue(button.isShown());
                    assertTrue(button.getBottom() <= ((android.view.View) button.getParent()).getHeight());
                });
                saveScreenshot(names[index].toLowerCase());

                AtomicReference<Intent> captured = new AtomicReference<>();
                Instrumentation.ActivityMonitor browserMonitor = new Instrumentation.ActivityMonitor() {
                    @Override public Instrumentation.ActivityResult onStartActivity(Intent intent) {
                        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                            captured.set(intent);
                            return new Instrumentation.ActivityResult(Activity.RESULT_OK, null);
                        }
                        return null;
                    }
                };
                instrumentation.addMonitor(browserMonitor);
                try {
                    instrumentation.runOnMainSync(() -> detail.findViewById(R.id.visit_site).performClick());
                    assertNotNull(captured.get());
                    assertEquals(Intent.ACTION_VIEW, captured.get().getAction());
                    assertEquals(urls[index], captured.get().getDataString());
                    assertNull("The website intent must be implicit", captured.get().getComponent());
                } finally {
                    instrumentation.removeMonitor(browserMonitor);
                    instrumentation.runOnMainSync(detail::finish);
                }
            } finally {
                instrumentation.removeMonitor(detailMonitor);
            }
        }
    }

    @Test public void invalidInputStaysOnMainScreen() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            for (String value : new String[]{"", "   ", "Guangzhou"}) {
                scenario.onActivity(activity -> {
                    EditText input = activity.findViewById(R.id.municipality_input);
                    input.setText(value);
                    input.onEditorAction(EditorInfo.IME_ACTION_GO);
                    assertNotNull(input.getError());
                    assertEquals(activity.getString(value.trim().isEmpty()
                            ? R.string.error_empty : R.string.error_unsupported), input.getError().toString());
                    assertFalse(activity.isFinishing());
                });
            }
        }
    }

    @Test public void missingAndUnsupportedExtrasCloseSafely() {
        for (String value : new String[]{null, "Guangzhou", " "}) {
            Intent intent = new Intent(instrumentation.getTargetContext(), Municipality.class);
            if (value != null) intent.putExtra(Municipality.EXTRA_MUNICIPALITY, value);
            try (ActivityScenario<Municipality> scenario = ActivityScenario.launch(intent)) {
                instrumentation.waitForIdleSync();
                assertEquals(androidx.lifecycle.Lifecycle.State.DESTROYED, scenario.getState());
            }
        }
    }

    @Test public void missingBrowserDoesNotCrashActivity() {
        Intent intent = new Intent(instrumentation.getTargetContext(), Municipality.class);
        intent.putExtra(Municipality.EXTRA_MUNICIPALITY, "Beijing");
        try (ActivityScenario<Municipality> scenario = ActivityScenario.launch(intent)) {
            Instrumentation.ActivityMonitor noBrowser = new Instrumentation.ActivityMonitor() {
                @Override public Instrumentation.ActivityResult onStartActivity(Intent outgoing) {
                    if (Intent.ACTION_VIEW.equals(outgoing.getAction())) {
                        throw new ActivityNotFoundException("No browser available in this test");
                    }
                    return null;
                }
            };
            instrumentation.addMonitor(noBrowser);
            try {
                scenario.onActivity(activity -> {
                    activity.findViewById(R.id.visit_site).performClick();
                    assertFalse(activity.isFinishing());
                });
            } finally {
                instrumentation.removeMonitor(noBrowser);
            }
        }
    }

    private void saveScreenshot(String name) throws Exception {
        instrumentation.waitForIdleSync();
        // A resumed activity can still be inside its window transition.
        instrumentation.getUiAutomation().waitForIdle(500, 5000);
        android.os.SystemClock.sleep(1000);
        Bitmap screenshot = instrumentation.getUiAutomation().takeScreenshot();
        assertNotNull(screenshot);
        File directory = new File(instrumentation.getTargetContext().getFilesDir(), "screenshots");
        assertTrue(directory.isDirectory() || directory.mkdirs());
        try (FileOutputStream output = new FileOutputStream(new File(directory, name + ".png"))) {
            assertTrue(screenshot.compress(Bitmap.CompressFormat.PNG, 100, output));
        } finally {
            screenshot.recycle();
        }
    }
}
