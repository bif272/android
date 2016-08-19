////////////////////////////////////////////////////////////////////////////////////////////////////
// A sandbox mobile app that provides users the ability to test their IoT devices
// Created On:  February 15, 2016
// Author:      Adeel Javed
// Website:     http://codifythings.com
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.codifythings.temperaturesensor;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by adeeljaved on 2/13/16.
 */
public class PreferencesManager {

    private String LOG = "{CodifyThings}";

    private String networkProtocol = "tcp";
    private String mqttBroker = null;
    private String mqttPort = null;
    private String mqttTopic = null;
    private String deviceId = null;
    private String mappingVariable = null;

    private String historicalData1 = null;
    private String historicalData2 = null;
    private String historicalData3 = null;
    private String historicalData4 = null;
    private String historicalData5 = null;

    private String lastUpdateDate = null;

    private SharedPreferences sharedPreferences = null;

    private static PreferencesManager preferencesManager = null;

    private PreferencesManager() {
        //Do nothing
    }

    public static PreferencesManager getInstance() {
        if (preferencesManager == null) {
            preferencesManager = new PreferencesManager();
        }

        return preferencesManager;
    }

    public void loadSharedPreferences(SharedPreferences sharedPreferences, Resources resources) {
        Log.i(LOG, "Loading Preferences");

        this.sharedPreferences = sharedPreferences;

        try {
            mqttBroker = sharedPreferences.getString("mqttBroker", resources.getString(R.string.default_mqtt_broker));
            mqttPort = sharedPreferences.getString("mqttPort", resources.getString(R.string.default_mqtt_port));
            mqttTopic = sharedPreferences.getString("mqttTopic", resources.getString(R.string.default_mqtt_topic));
            deviceId = sharedPreferences.getString("deviceId", resources.getString(R.string.default_mqtt_device));
            mappingVariable = sharedPreferences.getString("mappingVariable", resources.getString(R.string.default_mqtt_mapping));

            historicalData1 = sharedPreferences.getString("historicalData1", "NONE");
            historicalData2 = sharedPreferences.getString("historicalData2", "NONE");
            historicalData3 = sharedPreferences.getString("historicalData3", "NONE");
            historicalData4 = sharedPreferences.getString("historicalData4", "NONE");
            historicalData5 = sharedPreferences.getString("historicalData5", "NONE");

            lastUpdateDate = sharedPreferences.getString("lastUpdateDate", "N/A");

        } catch (Exception e) {
            Log.e(LOG, e.getMessage());
        }
    }

    public void saveHistoricalData(String historicalData) {
        Log.i(LOG, "Saving Historical Data");

        SharedPreferences.Editor editor = sharedPreferences.edit();

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String currentDate = dateFormat.format(new Date());

        editor.putString("lastUpdateDate", currentDate);

        if (historicalData1 == "NONE") {
            editor.putString("historicalData1", historicalData);
        } else if (historicalData2 == "NONE") {
            editor.putString("historicalData2", historicalData);
        } else if (historicalData3 == "NONE") {
            editor.putString("historicalData3", historicalData);
        } else if (historicalData4 == "NONE") {
            editor.putString("historicalData4", historicalData);
        } else if (historicalData5 == "NONE") {
            editor.putString("historicalData5", historicalData);
        } else {
            editor.putString("historicalData5", historicalData);
            editor.putString("historicalData4", historicalData5);
            editor.putString("historicalData3", historicalData4);
            editor.putString("historicalData2", historicalData3);
            editor.putString("historicalData1", historicalData2);
        }
        editor.commit();
    }

    public void saveServerSettings() {
        Log.i(LOG, "Saving Server Settings");

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("mqttBroker", mqttBroker);
        editor.putString("mqttPort", mqttPort);
        editor.putString("mqttTopic", mqttTopic);
        editor.putString("deviceId", deviceId);
        editor.putString("mappingVariable", mappingVariable);

        editor.commit();
    }

    public String getNetworkProtocol() {
        return networkProtocol;
    }

    public void setNetworkProtocol(String networkProtocol) {
        this.networkProtocol = networkProtocol;
    }

    public String getMqttBroker() {
        return mqttBroker;
    }

    public void setMqttBroker(String mqttBroker) {
        this.mqttBroker = mqttBroker;
    }

    public String getMqttPort() {
        return mqttPort;
    }

    public void setMqttPort(String mqttPort) {
        this.mqttPort = mqttPort;
    }

    public String getMqttTopic() {
        return mqttTopic;
    }

    public void setMqttTopic(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMappingVariable() {
        return mappingVariable;
    }

    public void setMappingVariable(String mappingVariable) {
        this.mappingVariable = mappingVariable;
    }

    public String getMQTTServer() {
        return networkProtocol + "://" + mqttBroker + ":" + mqttPort;
    }

    public String getHistoricalData1() {
        return historicalData1;
    }

    public void setHistoricalData1(String historicalData1) {
        this.historicalData1 = historicalData1;
    }

    public String getHistoricalData2() {
        return historicalData2;
    }

    public void setHistoricalData2(String historicalData2) {
        this.historicalData2 = historicalData2;
    }

    public String getHistoricalData3() {
        return historicalData3;
    }

    public void setHistoricalData3(String historicalData3) {
        this.historicalData3 = historicalData3;
    }

    public String getHistoricalData4() {
        return historicalData4;
    }

    public void setHistoricalData4(String historicalData4) {
        this.historicalData4 = historicalData4;
    }

    public String getHistoricalData5() {
        return historicalData5;
    }

    public void setHistoricalData5(String historicalData5) {
        this.historicalData5 = historicalData5;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
