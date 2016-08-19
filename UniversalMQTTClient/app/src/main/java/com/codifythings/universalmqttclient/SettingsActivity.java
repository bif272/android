package com.codifythings.universalmqttclient;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            Log.i(Constants.LOG, "onPreferenceChange: " + stringValue);

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof SwitchPreference) {

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        Log.i(Constants.LOG, "isXLargeTablet");

        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        Log.i(Constants.LOG, "bindPreferenceSummaryToValue");

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof SwitchPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), true));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG, "onCreate");

        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        Log.i(Constants.LOG, "setupActionBar");

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        Log.i(Constants.LOG, "onMenuItemSelected");

        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        Log.i(Constants.LOG, "onIsMultiPane");

        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        Log.i(Constants.LOG, "onBuildHeaders");

        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        Log.i(Constants.LOG, "isValidFragment");

        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || ServerPreferenceFragment.class.getName().equals(fragmentName)
                || MessagePreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || WidgetsPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * General Settings Fragment
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.i(Constants.LOG, "GeneralPreferenceFragment.onCreate");

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_APP_NAME));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_BACKGROUND_COLOR));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_TEXT_COLOR));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_HIGHLIGHTER_COLOR));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Log.i(Constants.LOG, "GeneralPreferenceFragment.onOptionsItemSelected");

            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Server Settings Fragment
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ServerPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.i(Constants.LOG, "ServerPreferenceFragment.onCreate");

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_server);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_MQTT_HOST));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_MQTT_PORT));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_MQTT_TOPIC));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_MQTT_BASIC_AUTHENTICATION_SWITCH));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_MQTT_USERNAME));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_MQTT_PASSWORD));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_MQTT_DEVICE));
//            DISABLED FOR RELEASE 1.2
//            bindPreferenceSummaryToValue(findPreference(Constants.PREF_MQTT_QOS));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Log.i(Constants.LOG, "ServerPreferenceFragment.onOptionsItemSelected");

            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Message Settings Fragment
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MessagePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.i(Constants.LOG, "MessagePreferenceFragment.onCreate");

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_message);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_DATA_FORMAT_SWITCH));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_DATA_KEY));

//            DISABLED FOR RELEASE 1.0
//            bindPreferenceSummaryToValue(findPreference(Constants.PREF_LONGITUDE_KEY));
//            bindPreferenceSummaryToValue(findPreference(Constants.PREF_LATITUDE_KEY));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Log.i(Constants.LOG, "MessagePreferenceFragment.onOptionsItemSelected");

            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Notifications Settings Fragment
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.i(Constants.LOG, "NotificationPreferenceFragment.onOptionsItemSelected");

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_NOTIFICATIONS_SWTICH));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Log.i(Constants.LOG, "NotificationPreferenceFragment.onOptionsItemSelected");

            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Widgets Settings Fragment
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class WidgetsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            Log.i(Constants.LOG, "WidgetsPreferenceFragment.onCreate");

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_widgets);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_ICON_LIBRARY_SWITCH));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_ICON_LIBRARY));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_ICON_TYPE));

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_CHART_SWITCH));
            bindPreferenceSummaryToValue(findPreference(Constants.PREF_CHART_TYPE));

//            DISABLED FOR RELEASE 1.0
//            bindPreferenceSummaryToValue(findPreference("switch_map"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Log.i(Constants.LOG, "WidgetsPreferenceFragment.onOptionsItemSelected");

            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
