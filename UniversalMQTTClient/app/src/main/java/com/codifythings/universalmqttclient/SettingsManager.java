package com.codifythings.universalmqttclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by adeeljaved on 7/29/16.
 */
public class SettingsManager {
    private Context context = null;
    private Resources resources = null;
    private SharedPreferences sharedPref = null;

    //General Settings
    private String appName = null;
    private int backgroundColor = 0;
    private int foregroundColor = 0;
    private int highlighterColor = 0;

    //Server Settings
    private String mqttHost = null;
    private String mqttPort = null;
    private String mqttTopic = null;
    private Boolean basicAuthenticationSwitch = null;
    private String mqttUsername = null;
    private String mqttPassword = null;
    private String deviceId = null;
    private String mqttQoS = null;

    //Message Settings
    private Boolean dataFormatSwitch = null;
    private String dataKey = null;
    private String sensorValue = null;
    private String longitudeKey = null;
    private String latitudeKey = null;
    private String longitudeValue = null;
    private String latitudeValue = null;

    //Notifications Settings
    private Boolean switchNotifications = null;

    //Widgets Settings
    private Boolean iconLibrarySwitch = null;
    private String iconLibrary = null;
    private String iconType = null;
    private Boolean chartSwitch = null;
    private String chartType = null;
    private Boolean mapSwitch = null;

    //Historical Data
    private String historicalData1 = null;
    private String historicalData2 = null;
    private String historicalData3 = null;
    private String historicalData4 = null;
    private String historicalData5 = null;
    private String lastUpdateDateStr = null;

    public SettingsManager(Context context) {
        Log.i(Constants.LOG, "SettingsManager");

        this.context = context;
        this.resources = context.getResources();

        //Load Shared Preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Log.i(Constants.LOG, "Loaded Shared Preferences: " + sharedPref.getAll());
    }

    /************************************************************************************************************************
     * General Settings
     ***********************************************************************************************************************/

    public String getAppName() {
        this.appName = sharedPref.getString(Constants.PREF_APP_NAME, resources.getString(R.string.pref_default_app_name));
        this.appName = this.appName.equals("") ? resources.getString(R.string.pref_default_app_name) : this.appName;

        return appName;
    }

    public int getBackgroundColor() {
        String backgroundColorStr = sharedPref.getString(Constants.PREF_BACKGROUND_COLOR, resources.getString(R.string.pref_default_background_color));

        try {
            this.backgroundColor = Color.parseColor(backgroundColorStr.equals("") ? resources.getString(R.string.pref_default_background_color) : backgroundColorStr);
        } catch (Exception ex) {
            this.backgroundColor = Color.parseColor(resources.getString(R.string.pref_default_background_color));
        }

        return backgroundColor;
    }

    public int getForegroundColor() {
        String foregroundColorStr = sharedPref.getString(Constants.PREF_TEXT_COLOR, resources.getString(R.string.pref_default_text_color));

        try {
            this.foregroundColor = Color.parseColor(foregroundColorStr.equals("") ? resources.getString(R.string.pref_default_text_color) : foregroundColorStr);
        } catch (Exception ex) {
            this.foregroundColor = Color.parseColor(resources.getString(R.string.pref_default_text_color));
        }

        return foregroundColor;
    }

    public int getHighlighterColor() {
        String highlighterColorStr = sharedPref.getString(Constants.PREF_HIGHLIGHTER_COLOR, resources.getString(R.string.pref_default_highlighter_color));

        try {
            this.highlighterColor = Color.parseColor(highlighterColorStr.equals("") ? resources.getString(R.string.pref_default_highlighter_color) : highlighterColorStr);
        } catch (Exception ex) {
            this.highlighterColor = Color.parseColor(resources.getString(R.string.pref_default_highlighter_color));
        }

        return highlighterColor;
    }

    /************************************************************************************************************************
     * Server Settings
     ***********************************************************************************************************************/

    public String getNetworkProtocol() {
        return Constants.PREF_MQTT_PROTOCOL;
    }

    public String getMqttHost() {
        this.mqttHost = sharedPref.getString(Constants.PREF_MQTT_HOST, resources.getString(R.string.pref_default_mqtt_host));
        this.mqttHost = this.mqttHost.equals("") ? resources.getString(R.string.pref_default_mqtt_host) : this.mqttHost;

        return mqttHost;
    }

    public String getMqttPort() {
        this.mqttPort = sharedPref.getString(Constants.PREF_MQTT_PORT, resources.getString(R.string.pref_default_mqtt_port));
        this.mqttPort = this.mqttPort.equals("") ? resources.getString(R.string.pref_default_mqtt_port) : this.mqttPort;

        return mqttPort;
    }

    public String getMqttHostFull() {
        return this.getNetworkProtocol() + "://" + this.getMqttHost() + ":" + this.getMqttPort();
    }

    public String getMqttTopic() {
        this.mqttTopic = sharedPref.getString(Constants.PREF_MQTT_TOPIC, resources.getString(R.string.pref_default_mqtt_topic));
        this.mqttTopic = this.mqttTopic.equals("") ? resources.getString(R.string.pref_default_mqtt_topic) : this.mqttTopic;

        return mqttTopic;
    }

    public Boolean getMqttBasicAuthenticationSwitch() {
        this.basicAuthenticationSwitch = sharedPref.getBoolean(Constants.PREF_MQTT_BASIC_AUTHENTICATION_SWITCH, false);

        return basicAuthenticationSwitch;
    }

    public String getMqttUsername() {
        this.mqttUsername = sharedPref.getString(Constants.PREF_MQTT_USERNAME, resources.getString(R.string.pref_default_mqtt_username));

//        ENHANCEMENT - Commented code because value of basic authentication switch will be used to check if username/password needs to be sent
//        this.mqttUsername = this.mqttUsername.equals("") ? resources.getString(R.string.pref_default_mqtt_username) : this.mqttUsername;

        return mqttUsername;
    }

    public char[] getMqttPassword() {
        this.mqttPassword = sharedPref.getString(Constants.PREF_MQTT_PASSWORD, resources.getString(R.string.pref_default_mqtt_password));

//        BUG FIX - Commented code because platforms such as Carriots IoT, require password to be
//                  sent as empty
//        this.mqttPassword = this.mqttPassword.equals("") ? resources.getString(R.string.pref_default_mqtt_password) : this.mqttPassword;

        return mqttPassword.toCharArray();
    }

    public String getDeviceId() {
        this.deviceId = sharedPref.getString(Constants.PREF_MQTT_DEVICE, resources.getString(R.string.pref_default_mqtt_device));
        this.deviceId = this.deviceId.equals("") ? resources.getString(R.string.pref_default_mqtt_device) : this.deviceId;

        return deviceId;
    }

    public String getMqttQoS() {
        this.mqttQoS = sharedPref.getString(Constants.PREF_MQTT_QOS, resources.getString(R.string.pref_default_mqtt_qos));
        this.mqttQoS = this.mqttQoS.equals("") ? resources.getString(R.string.pref_default_mqtt_qos) : this.mqttQoS;

        return mqttQoS;
    }

    /************************************************************************************************************************
     * Message Settings
     ***********************************************************************************************************************/

    public Boolean getDataFormatSwitch() {
        this.dataFormatSwitch = sharedPref.getBoolean(Constants.PREF_DATA_FORMAT_SWITCH, false);

        return dataFormatSwitch;
    }

    public String getDataKey() {
        this.dataKey = sharedPref.getString(Constants.PREF_DATA_KEY, resources.getString(R.string.pref_default_data_key));
        this.dataKey = this.dataKey.equals("") ? resources.getString(R.string.pref_default_data_key) : this.dataKey;

        return dataKey;
    }

    public String getLongitudeKey() {
        this.longitudeKey = sharedPref.getString(Constants.PREF_LONGITUDE_KEY, resources.getString(R.string.pref_default_longitude_key));
        this.longitudeKey = this.longitudeKey.equals("") ? resources.getString(R.string.pref_default_longitude_key) : this.longitudeKey;

        return longitudeKey;
    }

    public String getLatitudeKey() {
        this.latitudeKey = sharedPref.getString(Constants.PREF_LATITUDE_KEY, resources.getString(R.string.pref_default_latitude_key));
        this.latitudeKey = this.latitudeKey.equals("") ? resources.getString(R.string.pref_default_latitude_key) : this.latitudeKey;

        return latitudeKey;
    }

    /************************************************************************************************************************
     * Notifications Settings
     ***********************************************************************************************************************/

    public Boolean getSwitchNotifications() {
        this.switchNotifications = sharedPref.getBoolean(Constants.PREF_NOTIFICATIONS_SWTICH, false);

        return switchNotifications;
    }

    /************************************************************************************************************************
     * Widgets Settings
     ***********************************************************************************************************************/

    public Boolean getIconLibrarySwitch() {
        this.iconLibrarySwitch = sharedPref.getBoolean(Constants.PREF_ICON_LIBRARY_SWITCH, true);

        return iconLibrarySwitch;
    }

    public String getIconLibrary() {
        this.iconLibrary = sharedPref.getString(Constants.PREF_ICON_LIBRARY, resources.getString(R.string.pref_default_icon_library));

        return iconLibrary;
    }

    public String getIconType() {
        this.iconType = sharedPref.getString(Constants.PREF_ICON_TYPE, resources.getString(R.string.pref_default_icon_type));
        this.iconType = this.iconType.equals("") ? resources.getString(R.string.pref_default_icon_type) : this.iconType;
        this.iconType = Constants.PREF_ICON_TYPE_PREFIX + this.iconType + Constants.PREF_ICON_TYPE_SUFFIX;

        return iconType;
    }

    public Boolean getChartSwitch() {
        this.chartSwitch = sharedPref.getBoolean(Constants.PREF_CHART_SWITCH, true);

        return chartSwitch;
    }

    public String getChartType() {
        this.chartType = sharedPref.getString(Constants.PREF_CHART_TYPE, resources.getString(R.string.pref_default_chart_type));

        return chartType;
    }

    public Boolean getMapSwitch() {
        this.mapSwitch = sharedPref.getBoolean(Constants.PREF_MAP_SWITCH, true);

        return mapSwitch;
    }

    /************************************************************************************************************************
     * Runtime Values
     ***********************************************************************************************************************/

    public String getLastUpdateDateStr() {
        this.lastUpdateDateStr = sharedPref.getString(Constants.VALUE_LAST_UPDATE_DATE, Constants.VALUE_NOT_APPLICABLE);

        return lastUpdateDateStr;
    }

    public String getHistoricalData1() {
        this.historicalData1 = sharedPref.getString(Constants.VALUE_HISTORICAL_DATA_1, Constants.VALUE_NONE);

        return historicalData1;
    }

    public String getHistoricalData2() {
        this.historicalData2 = sharedPref.getString(Constants.VALUE_HISTORICAL_DATA_2, Constants.VALUE_NONE);

        return historicalData2;
    }

    public String getHistoricalData3() {
        this.historicalData3 = sharedPref.getString(Constants.VALUE_HISTORICAL_DATA_3, Constants.VALUE_NONE);

        return historicalData3;
    }

    public String getHistoricalData4() {
        this.historicalData4 = sharedPref.getString(Constants.VALUE_HISTORICAL_DATA_4, Constants.VALUE_NONE);

        return historicalData4;
    }

    public String getHistoricalData5() {
        this.historicalData5 = sharedPref.getString(Constants.VALUE_HISTORICAL_DATA_5, Constants.VALUE_NONE);

        return historicalData5;
    }

    public String getLongitudeValue() {
        this.longitudeValue = sharedPref.getString(Constants.VALUE_LONGITUDE, resources.getString(R.string.pref_default_longitude_value));
        this.longitudeValue = this.longitudeValue.equals("") ? resources.getString(R.string.pref_default_longitude_value) : this.longitudeValue;

        return longitudeValue;
    }

    public String getLatitudeValue() {
        this.latitudeValue = sharedPref.getString(Constants.VALUE_LATITUDE, resources.getString(R.string.pref_default_latitude_value));
        this.latitudeValue = this.latitudeValue.equals("") ? resources.getString(R.string.pref_default_latitude_value) : this.latitudeValue;

        return latitudeValue;
    }

    public void saveHistoricalData(String historicalData, String longitudeValue, String latitudeValue) {
        Log.i(Constants.LOG, "saveHistoricalData");

        SharedPreferences.Editor editor = sharedPref.edit();

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String currentDate = dateFormat.format(new Date());

        editor.putString("lastUpdateDate", currentDate);

//        DISABLED FOR RELEASE 1.0
//        editor.putString("longitudeValue", longitudeValue);
//        editor.putString("latitudeValue", latitudeValue);

        if (this.historicalData1 == "NONE") {
            editor.putString("historicalData1", historicalData);
        } else if (this.historicalData2 == "NONE") {
            editor.putString("historicalData2", historicalData);
        } else if (this.historicalData3 == "NONE") {
            editor.putString("historicalData3", historicalData);
        } else if (this.historicalData4 == "NONE") {
            editor.putString("historicalData4", historicalData);
        } else if (this.historicalData5 == "NONE") {
            editor.putString("historicalData5", historicalData);
        } else {
            editor.putString("historicalData5", historicalData);
            editor.putString("historicalData4", this.historicalData5);
            editor.putString("historicalData3", this.historicalData4);
            editor.putString("historicalData2", this.historicalData3);
            editor.putString("historicalData1", this.historicalData2);
        }
        editor.commit();
    }
}
