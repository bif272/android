////////////////////////////////////////////////////////////////////////////////////////////////////
// A sandbox mobile app that provides users the ability to test their IoT devices
// Created On:  February 15, 2016
// Author:      Adeel Javed
// Website:     http://codifythings.com
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.codifythings.humiditysensor;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private String LOG = "{CodifyThings}";

    private TextView txtMqttBroker = null;
    private TextView txtMqttPort = null;
    private TextView txtMqttTopic = null;
    private TextView txtDeviceId = null;
    private TextView txtMappingVariable = null;

    private Button btnReset = null;
    private Button btnSave = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtMqttBroker = (TextView) findViewById(R.id.text_mqtt_broker);
        txtMqttPort = (TextView) findViewById(R.id.text_mqtt_port);
        txtMqttTopic = (TextView) findViewById(R.id.text_mqtt_topic);
        txtDeviceId = (TextView) findViewById(R.id.text_mqtt_device);
        txtMappingVariable = (TextView) findViewById(R.id.text_mqtt_mapping);

        btnReset = (Button) findViewById(R.id.button_reset);
        btnSave = (Button) findViewById(R.id.button_save);

        loadPreferences();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtMqttBroker.setText(PreferencesManager.getInstance().getMqttBroker());
                txtMqttPort.setText(PreferencesManager.getInstance().getMqttPort());
                txtMqttTopic.setText(PreferencesManager.getInstance().getMqttTopic());
                txtDeviceId.setText(PreferencesManager.getInstance().getDeviceId());
                txtMappingVariable.setText(PreferencesManager.getInstance().getMappingVariable());

                Snackbar.make(v, "Server Settings Reset", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesManager.getInstance().setMqttBroker(txtMqttBroker.getText().toString());
                PreferencesManager.getInstance().setMqttPort(txtMqttPort.getText().toString());
                PreferencesManager.getInstance().setMqttTopic(txtMqttTopic.getText().toString());
                PreferencesManager.getInstance().setDeviceId(txtDeviceId.getText().toString());
                PreferencesManager.getInstance().setMappingVariable(txtMappingVariable.getText().toString());

                PreferencesManager.getInstance().saveServerSettings();

                Snackbar.make(v, "Server Settings Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadPreferences() {
        Log.i(LOG, "Setting Preferences");

        PreferencesManager.getInstance().loadSharedPreferences(
                getSharedPreferences(getResources().getString(R.string.preferences_file_key),
                        Context.MODE_PRIVATE), getResources());

        txtMqttBroker.setText(PreferencesManager.getInstance().getMqttBroker());
        txtMqttPort.setText(PreferencesManager.getInstance().getMqttPort());
        txtMqttTopic.setText(PreferencesManager.getInstance().getMqttTopic());
        txtDeviceId.setText(PreferencesManager.getInstance().getDeviceId());
        txtMappingVariable.setText(PreferencesManager.getInstance().getMappingVariable());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
