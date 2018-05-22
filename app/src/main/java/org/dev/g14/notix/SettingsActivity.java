package org.dev.g14.notix;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;

// https://medium.com/@arasthel92/dynamically-creating-preferences-on-android-ecc56e4f0789
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        if (savedInstanceState == null) {
            Fragment preferenceFragment = new PrefFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.prefs_fragments_container, preferenceFragment);
            fragmentTransaction.commit();
        }

    }

    public static class PrefFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            // Load the Preferences from the XML file
            addPreferencesFromResource(R.xml.preferences_fragment);
        }
    }
}
