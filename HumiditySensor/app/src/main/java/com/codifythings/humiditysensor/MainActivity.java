////////////////////////////////////////////////////////////////////////////////////////////////////
// A sandbox mobile app that provides users the ability to test their IoT devices
// Created On:  February 15, 2016
// Author:      Adeel Javed
// Website:     http://codifythings.com
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.codifythings.humiditysensor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    private String LOG = "{CodifyThings}";

    private TextView fontIcon = null;
    private TextView sensorData = null;
    private TextView lastUpdateDate = null;

    private String mqttBroker = null;
    private String mqttTopic = null;
    private String deviceId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fontIcon = (TextView) findViewById(R.id.fontIcon);
        Typeface font = Typeface.createFromAsset(getAssets(), "weathericons-regular-webfont.ttf");
        fontIcon.setTypeface(font);
        sensorData = (TextView) findViewById(R.id.sensorData);
        lastUpdateDate = (TextView) findViewById(R.id.dateTime);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initSharedPreferences();

        setSensorValue(null);

        connectToMQTT();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG, "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(LOG, "Menu Item Selected");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i(LOG, "Opening Settings Activity");

            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            Log.i(LOG, "Opening About Activity");

            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshActivity() {
        Log.i(LOG, "Refreshing Activity");

        finish();
        startActivity(getIntent());
    }

    public void initSharedPreferences() {
        Log.i(LOG, "Loading Preferences");

        PreferencesManager.getInstance().loadSharedPreferences(getSharedPreferences(
                        getResources().getString(R.string.preferences_file_key), Context.MODE_PRIVATE),
                getResources());
    }

    public void setSensorValue(String sensorValue) {

        if (PreferencesManager.getInstance().getHistoricalData5() != "NONE") {
            sensorValue = PreferencesManager.getInstance().getHistoricalData5();
        }
        else if (PreferencesManager.getInstance().getHistoricalData4() != "NONE") {
            sensorValue = PreferencesManager.getInstance().getHistoricalData4();
        }
        else if (PreferencesManager.getInstance().getHistoricalData3() != "NONE") {
            sensorValue = PreferencesManager.getInstance().getHistoricalData3();
        }
        else if (PreferencesManager.getInstance().getHistoricalData2() != "NONE") {
            sensorValue = PreferencesManager.getInstance().getHistoricalData2();
        }
        else {
            sensorValue = PreferencesManager.getInstance().getHistoricalData1();
        }

        Log.i(LOG, "Setting Sensor Value: " + sensorValue);

        sensorData.setText(sensorValue);

        lastUpdateDate.setText(PreferencesManager.getInstance().getLastUpdateDate());

        createHistoricalChart();
    }

    public void createHistoricalChart() {
        Log.i(LOG, "Creating Historical Chart");

        LineChart chart = (LineChart) findViewById(R.id.chart);

        ArrayList<String> labels = new ArrayList<String>(); // chart labels
        ArrayList<Entry> entries = new ArrayList<>(); // chart entries

        if (!PreferencesManager.getInstance().getHistoricalData1().equals("NONE")) {
            labels.add("1");
            entries.add(new Entry(Float.parseFloat(PreferencesManager.getInstance().getHistoricalData1()), 0));
        }

        if (!PreferencesManager.getInstance().getHistoricalData2().equals("NONE")) {
            labels.add("2");
            entries.add(new Entry(Float.parseFloat(PreferencesManager.getInstance().getHistoricalData2()), 1));
        }

        if (!PreferencesManager.getInstance().getHistoricalData3().equals("NONE")) {
            labels.add("3");
            entries.add(new Entry(Float.parseFloat(PreferencesManager.getInstance().getHistoricalData3()), 2));
        }

        if (!PreferencesManager.getInstance().getHistoricalData4().equals("NONE")) {
            labels.add("4");
            entries.add(new Entry(Float.parseFloat(PreferencesManager.getInstance().getHistoricalData4()), 3));
        }

        if (!PreferencesManager.getInstance().getHistoricalData5().equals("NONE")) {
            labels.add("5");
            entries.add(new Entry(Float.parseFloat(PreferencesManager.getInstance().getHistoricalData5()), 4));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setDrawCubic(true);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ContextCompat.getColor(getApplicationContext(), R.color.hightlightColor));
        dataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.fontColor));

        LineData data = new LineData(labels, dataSet);
        chart.setData(data); // set the data and list of lables into chart

        chart.setDescription("Historical Data");  // set the description

        chart.setDrawGridBackground(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.getAxisLeft().setEnabled(false);

        chart.getAxisLeft().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.fontColor)); // left y-axis
        chart.getAxisRight().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.fontColor)); // left y-axis
        chart.getXAxis().setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.fontColor));

        Legend chartLegend = chart.getLegend();
        chartLegend.setEnabled(false);
    }

    private void connectToMQTT() {

        this.mqttBroker = PreferencesManager.getInstance().getMQTTServer();
        this.deviceId = PreferencesManager.getInstance().getDeviceId();
        this.mqttTopic = PreferencesManager.getInstance().getMqttTopic();

        try {
            // Request clean session in the connection options.
            Log.i(LOG, "Setting Connection Options");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            // Attempt a connection to MQTT broker using the values of connection variables.
            Log.i(LOG, "Connecting to MQTT Broker: " + mqttBroker);
            MqttClient client = new MqttClient(mqttBroker, deviceId, new MemoryPersistence());
            client.connect(options);

            // Set callback method name that will be invoked when a new message is posted to topic,
            // MqttEventCallback class is defined later in the code.
            Log.i(LOG, "Subscribing to Topic: " + mqttTopic);
            client.setCallback(this);
            client.subscribe(mqttTopic, 0);
        } catch (Exception ex) {
            Log.e(LOG, ex.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable arg0) {
        // Do nothing
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        // Do nothing
    }

    @Override
    public void messageArrived(String topic, final MqttMessage msg) throws Exception {
        Log.i(LOG, "New Message Arrived from Topic: " + topic);

        try {
            String sensorData = new String(msg.getPayload());

            if (sensorData != null && sensorData != "") {
                JSONObject jsonObject = new JSONObject(sensorData);
                String sensorValue = jsonObject.getString(PreferencesManager.getInstance().getMappingVariable());

                PreferencesManager.getInstance().saveHistoricalData(sensorValue);

                refreshActivity();
            }
        } catch (Exception ex) {
            Log.e(LOG, ex.getMessage());
        }
    }
}