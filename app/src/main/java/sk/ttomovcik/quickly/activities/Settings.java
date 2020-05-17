package sk.ttomovcik.quickly.activities;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.pixplicity.easyprefs.library.Prefs;

import eu.dkaratzas.android.inapp.update.Constants;
import eu.dkaratzas.android.inapp.update.InAppUpdateManager;
import sk.ttomovcik.quickly.R;

import static java.util.Objects.requireNonNull;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        Preference prefCheckForUpdates, prefRestartApp;
        SwitchPreference prefEnableShareSignature, prefDisableShareBtn, prefExpFeat;
        ListPreference prefFabPos, prefAppTheme;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            new Prefs.Builder().setContext(getContext())
                    .setMode(ContextWrapper.MODE_PRIVATE)
                    .setPrefsName(requireNonNull(getContext()).getPackageName())
                    .setUseDefaultSharedPreference(true).build();

            prefCheckForUpdates = findPreference("checkForUpdates");
            prefEnableShareSignature = findPreference("enableShareSignature");
            prefDisableShareBtn = findPreference("disableShareButton");
            prefExpFeat = findPreference("enableExperimentalFeatures");
            prefFabPos = findPreference("fabPosition");
            prefAppTheme = findPreference("appTheme");
            prefRestartApp = findPreference("restartApp");

            prefCheckForUpdates.setOnPreferenceClickListener(preference -> {
                Log.d("Settings", "Checking for updates");
                InAppUpdateManager.Builder((AppCompatActivity) getActivity(), 100)
                        .resumeUpdates(true)
                        .mode(Constants.UpdateMode.IMMEDIATE)
                        .checkForAppUpdate();
                return true;
            });

            if (Prefs.getBoolean("enableShareSignature", false)) {
                prefEnableShareSignature.setChecked(true);
            }
            if (Prefs.getBoolean("disableShareButton", false)) {
                prefDisableShareBtn.setChecked(true);
                prefEnableShareSignature.setEnabled(false);
            }
            prefDisableShareBtn.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!Prefs.getBoolean("disableShareButton", true)) {
                    prefEnableShareSignature.setChecked(false);
                    prefEnableShareSignature.setEnabled(false);
                } else {
                    prefEnableShareSignature.setEnabled(true);
                }
                return true;
            });
            prefRestartApp.setOnPreferenceClickListener(preference -> {
                Intent i = getContext().getPackageManager().
                        getLaunchIntentForPackage(getContext().getPackageName());
                assert i != null;
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                return true;
            });
        }
    }
}