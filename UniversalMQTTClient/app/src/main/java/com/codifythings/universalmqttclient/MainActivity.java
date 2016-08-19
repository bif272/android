package com.codifythings.universalmqttclient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MqttCallback, OnMapReadyCallback {

    // Reference to app settings
    private SettingsManager settingsManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Preferences
        settingsManager = new SettingsManager(this);

        // Initialize Screen Elements
        initScreenElements();

        // Set Initial Sensor Value
        initSensorData(null);

        // Connect to MQTT Server
        connectToMQTT();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(Constants.LOG, "onCreateOptionsMenu");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(Constants.LOG, "onOptionsItemSelected");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));

            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initScreenElements() {
        Log.i(Constants.LOG, "initScreenElements");

        // Initialize General Settings
        initGeneralSettings();

        // Initialize Icon
        initIcon();

        // Initialize Sensor Data
        initSensorData(null);

        // Initialize Historical Data Chart
        initHistoricalChart();

        // Initialize Map
        initMap();
    }

    private void initGeneralSettings() {
        Log.i(Constants.LOG, "initGeneralSettings");

        // Set Screen Background
        //getWindow().getDecorView().setBackgroundColor(settingsManager.getBackgroundColor());
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mainLayout.setBackgroundColor(settingsManager.getBackgroundColor());

        // Initialize & Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(settingsManager.getAppName());
        toolbar.setBackgroundColor(settingsManager.getBackgroundColor());
        toolbar.setTitleTextColor(settingsManager.getForegroundColor());
        setSupportActionBar(toolbar);
    }

    private void initIcon() {
        Log.i(Constants.LOG, "initIcon");

        TextView fontIcon = (TextView) findViewById(R.id.fontIcon);

        if (settingsManager.getIconLibrarySwitch()) {
            fontIcon.setVisibility(View.VISIBLE);
            fontIcon.setTextColor(settingsManager.getForegroundColor());
            fontIcon.setText(Html.fromHtml(settingsManager.getIconType()));
            Typeface font = Typeface.createFromAsset(getAssets(), settingsManager.getIconLibrary());
            fontIcon.setTypeface(font);

        } else {
            fontIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void initSensorData(String sensorValue) {
        Log.i(Constants.LOG, "initSensorData: " + sensorValue + " - " + settingsManager.getLastUpdateDateStr());

        TextView sensorData;
        TextView lastUpdateDate;

        if (settingsManager.getHistoricalData5() != Constants.VALUE_NONE) {
            sensorValue = settingsManager.getHistoricalData5();
        } else if (settingsManager.getHistoricalData4() != Constants.VALUE_NONE) {
            sensorValue = settingsManager.getHistoricalData4();
        } else if (settingsManager.getHistoricalData3() != Constants.VALUE_NONE) {
            sensorValue = settingsManager.getHistoricalData3();
        } else if (settingsManager.getHistoricalData2() != Constants.VALUE_NONE) {
            sensorValue = settingsManager.getHistoricalData2();
        } else {
            sensorValue = settingsManager.getHistoricalData1();
        }

        sensorData = (TextView) findViewById(R.id.sensorData);
        sensorData.setTextColor(settingsManager.getForegroundColor());
        sensorData.setText(sensorValue);

        lastUpdateDate = (TextView) findViewById(R.id.dateTime);
        lastUpdateDate.setTextColor(settingsManager.getHighlighterColor());
        lastUpdateDate.setText(settingsManager.getLastUpdateDateStr());
    }

    public void initHistoricalChart() {
        Log.i(Constants.LOG, "initHistoricalChart");

        LineChart lineChart = (LineChart) findViewById(R.id.lineChart);
        BarChart barChart = (BarChart) findViewById(R.id.barChart);

        if (settingsManager.getChartSwitch()) {
            if (settingsManager.getChartType().equals("Line Chart")) {
                barChart.setVisibility(View.GONE);
                lineChart.setVisibility(View.VISIBLE);

                initLineChart(lineChart);

            } else {
                lineChart.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);

                initBarChart(barChart);
            }

        } else {
            lineChart.setVisibility(View.GONE);
            barChart.setVisibility(View.GONE);
        }
    }

    private void initLineChart(LineChart lineChart) {
        Log.i(Constants.LOG, "initLineChart");

        ArrayList<String> labels = new ArrayList<String>(); // chart labels
        ArrayList<Entry> entries = new ArrayList<>(); // chart entries

        if (!settingsManager.getHistoricalData1().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData1())) {
            labels.add("1");
            entries.add(new Entry(Float.parseFloat(settingsManager.getHistoricalData1()), 0));
        }

        if (!settingsManager.getHistoricalData2().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData2())) {
            labels.add("2");
            entries.add(new Entry(Float.parseFloat(settingsManager.getHistoricalData2()), 1));
        }

        if (!settingsManager.getHistoricalData3().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData3())) {
            labels.add("3");
            entries.add(new Entry(Float.parseFloat(settingsManager.getHistoricalData3()), 2));
        }

        if (!settingsManager.getHistoricalData4().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData4())) {
            labels.add("4");
            entries.add(new Entry(Float.parseFloat(settingsManager.getHistoricalData4()), 3));
        }

        if (!settingsManager.getHistoricalData5().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData5())) {
            labels.add("5");
            entries.add(new Entry(Float.parseFloat(settingsManager.getHistoricalData5()), 4));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setDrawCubic(true);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(settingsManager.getHighlighterColor());
        dataSet.setValueTextColor(settingsManager.getForegroundColor());
        dataSet.setColor(settingsManager.getForegroundColor());

        LineData data = new LineData(labels, dataSet);
        lineChart.setData(data); // set the data and list of labels into chart

        lineChart.setDescription("Historical Data");  // set the description

        lineChart.setDrawGridBackground(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);

        lineChart.getAxisLeft().setTextColor(settingsManager.getForegroundColor()); // left y-axis
        lineChart.getAxisRight().setTextColor(settingsManager.getForegroundColor()); // left y-axis
        lineChart.getXAxis().setTextColor(settingsManager.getForegroundColor());

        Legend chartLegend = lineChart.getLegend();
        chartLegend.setEnabled(false);
    }


    private void initBarChart(BarChart barChart) {
        Log.i(Constants.LOG, "initBarChart");

        ArrayList<String> labels = new ArrayList<String>(); // chart labels
        ArrayList<BarEntry> entries = new ArrayList<>(); // chart entries

        if (!settingsManager.getHistoricalData1().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData1())) {
            labels.add("1");
            entries.add(new BarEntry(Float.parseFloat(settingsManager.getHistoricalData1()), 0));
        }

        if (!settingsManager.getHistoricalData2().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData2())) {
            labels.add("2");
            entries.add(new BarEntry(Float.parseFloat(settingsManager.getHistoricalData2()), 1));
        }

        if (!settingsManager.getHistoricalData3().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData3())) {
            labels.add("3");
            entries.add(new BarEntry(Float.parseFloat(settingsManager.getHistoricalData3()), 2));
        }

        if (!settingsManager.getHistoricalData4().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData4())) {
            labels.add("4");
            entries.add(new BarEntry(Float.parseFloat(settingsManager.getHistoricalData4()), 3));
        }

        if (!settingsManager.getHistoricalData5().equals(Constants.VALUE_NONE) && isNumeric(settingsManager.getHistoricalData5())) {
            labels.add("5");
            entries.add(new BarEntry(Float.parseFloat(settingsManager.getHistoricalData5()), 4));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setValueTextColor(settingsManager.getForegroundColor());
        dataSet.setColor(settingsManager.getHighlighterColor());

        BarData data = new BarData(labels, dataSet);
        barChart.setData(data); // set the data and list of lables into chart

        barChart.setDescription("Historical Data");  // set the description

        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);

        barChart.getAxisLeft().setTextColor(settingsManager.getForegroundColor()); // left y-axis
        barChart.getAxisRight().setTextColor(settingsManager.getForegroundColor()); // left y-axis
        barChart.getXAxis().setTextColor(settingsManager.getForegroundColor());

        Legend chartLegend = barChart.getLegend();
        chartLegend.setEnabled(false);
    }

    private void initMap() {
//        DISABLED FOR RELEASE 1.0
//        Log.i(Constants.LOG, "initMap");
//        TextView mapEmptySection = (TextView) findViewById(R.id.empty2);
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//
//        this.createHistoricalChart();
//
//        if (this.switchMap) {
//            Log.i(Constants.LOG, "Map Section Visible");
//            mapFragment.getMapAsync(this);
//            mapEmptySection.setVisibility(View.VISIBLE);
//            mapFragment.getView().setVisibility(View.VISIBLE);
//        } else {
//            Log.i(Constants.LOG, "Map Section Invisible");
//            mapEmptySection.setVisibility(View.INVISIBLE);
//            mapFragment.getView().setVisibility(View.INVISIBLE);
//        }
    }

    private void connectToMQTT() {
        Log.i(Constants.LOG, "connectToMQTT");

        try {
            // Request clean session in the connection options.
            Log.i(Constants.LOG, "Setting Connection Options");
            MqttConnectOptions options = new MqttConnectOptions();

            if(settingsManager.getMqttBasicAuthenticationSwitch()) {
//                ENHANCEMENT - Commented code because value of basic authentication switch will be used to check if username/password needs to be sent
//                if (!settingsManager.getMqttUsername().equals("Optional")) {
                    options.setUserName(settingsManager.getMqttUsername());
//                }
//
//                if (!settingsManager.getMqttPassword().equals("Optional")) {
                    options.setPassword(settingsManager.getMqttPassword());
//                }
            }

            options.setCleanSession(true);

            // Attempt a connection to MQTT broker using the values of connection variables.
            Log.i(Constants.LOG, "Connecting to MQTT Broker: " + settingsManager.getMqttHostFull());
            MqttClient client = new MqttClient(settingsManager.getMqttHostFull(), settingsManager.getDeviceId(), new MemoryPersistence());
            client.connect(options);

            // Set callback method name that will be invoked when a new message is posted to topic,
            // MqttEventCallback class is defined later in the code.
            Log.i(Constants.LOG, "Subscribing to Topic: " + settingsManager.getMqttTopic());
            client.setCallback(this);
            client.subscribe(settingsManager.getMqttTopic(), new Integer(settingsManager.getMqttQoS()));
        } catch (Exception ex) {
            Log.e(Constants.LOG, ex.getMessage());
            ex.printStackTrace();

            createSnackBar("Error connecting to MQTT server " + settingsManager.getMqttHostFull());
        }
    }

    private void createNotification(String notificationTitle, String notificationMessage) {
        Log.i(Constants.LOG, "createNotification");

        if (settingsManager.getSwitchNotifications()) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(notificationTitle)
                            .setContentText(notificationMessage);

            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(getApplicationContext(),
                    MainActivity.class);

            // The stack builder object will contain an artificial back
            // stack for the started Activity. This ensures that navigating
            // backward from the Activity leads out of your application to the
            // Home screen.
            TaskStackBuilder stackBuilder =
                    TaskStackBuilder.create(getApplicationContext());

            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);

            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            // mId allows you to update the notification later on.
            mNotificationManager.notify(100, mBuilder.build());
        }
    }

    private void createSnackBar(String message) {
        Snackbar.make(findViewById(R.id.mainLayout), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(Constants.LOG, "onMapReady");

//        DISABLED FOR RELEASE 1.0
//        GoogleMap mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(new Integer(settingsManager.getLatitudeValue()).intValue(), new Integer(settingsManager.getLongitudeValue()).intValue());
//        mMap.addMarker(new MarkerOptions().position(sydney).title(Constants.VALUE_DEVICE));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.MAP_ZOOM_LEVEL));
    }

    @Override
    public void connectionLost(Throwable arg0) {
        Log.i(Constants.LOG, "connectionLost");

        // Do nothing
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {
        Log.i(Constants.LOG, "deliveryComplete");

        // Do nothing
    }

    private void refreshActivity() {
        Log.i(Constants.LOG, "refreshActivity");

        finish();
        startActivity(getIntent());
    }

    @Override
    public void messageArrived(String topic, final MqttMessage msg) throws Exception {
        Log.i(Constants.LOG, "messageArrived: " + topic);

        try {
            String sensorData = new String(msg.getPayload());

            String sensorValue = null;
            String longitudeValue = null;
            String latitudeValue = null;

            if (sensorData != null && sensorData != "") {
                if (settingsManager.getDataFormatSwitch()) {
                    JSONObject jsonObject = new JSONObject(sensorData);

                    try {
                        sensorValue = jsonObject.getString(settingsManager.getDataKey());
                    } catch (Exception ex) {
                        Log.e(Constants.LOG, ex.getMessage());
                    }
                    try {
                        longitudeValue = jsonObject.getString(settingsManager.getLongitudeKey());
                    } catch (Exception ex) {
                        Log.e(Constants.LOG, ex.getMessage());
                    }
                    try {
                        latitudeValue = jsonObject.getString(settingsManager.getLatitudeKey());
                    } catch (Exception ex) {
                        Log.e(Constants.LOG, ex.getMessage());
                    }
                } else {
                    try {
                        sensorValue = sensorData;
                    } catch (Exception ex) {
                        Log.e(Constants.LOG, ex.getMessage());
                    }
                }

                settingsManager.saveHistoricalData(sensorValue, longitudeValue, latitudeValue);

                createNotification(settingsManager.getAppName(), Constants.NOTIFICATIO_MESSAGE);

                refreshActivity();
            }
        } catch (Exception ex) {
            Log.e(Constants.LOG, ex.getMessage());

            createSnackBar("Error receiving message from MQTT server " + ex.getMessage());
        }
    }

    private boolean isNumeric(String sensorValue) {
        Log.i(Constants.LOG, "isNumeric: " + sensorValue);

        boolean isNumeric = false;

        try {
            Integer.parseInt(sensorValue);

            isNumeric = true;
        } catch (Exception ex) {
            // Do nothing
        }

        try {
            Float.parseFloat(sensorValue);

            isNumeric = true;
        } catch (Exception ex) {
            // Do nothing
        }

        return isNumeric;
    }
}