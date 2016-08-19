package com.codifythings.universalmqttclient;

import android.graphics.Color;

/**
 * Created by adeeljaved on 7/29/16.
 */
public interface Constants {
    public String LOG = "{CodifyThings}";

    //General Settings
    String PREF_APP_NAME = "text_app_name";
    String PREF_BACKGROUND_COLOR = "text_background_color";
    String PREF_TEXT_COLOR = "text_text_color";
    String PREF_HIGHLIGHTER_COLOR = "text_highlighter_color";

    //Server Settings
    String PREF_MQTT_PROTOCOL = "tcp";
    String PREF_MQTT_HOST = "text_mqtt_host";
    String PREF_MQTT_PORT = "text_mqtt_port";
    String PREF_MQTT_TOPIC = "text_mqtt_topic";
    String PREF_MQTT_BASIC_AUTHENTICATION_SWITCH = "switch_basic_authentication";
    String PREF_MQTT_USERNAME = "text_mqtt_username";
    String PREF_MQTT_PASSWORD = "text_mqtt_password";
    String PREF_MQTT_DEVICE = "text_mqtt_device";
    String PREF_MQTT_QOS = "list_mqtt_qos";

    //Message Settings
    String PREF_DATA_FORMAT_SWITCH = "switch_data_format";
    String PREF_DATA_KEY = "text_data_key";
    String PREF_LONGITUDE_KEY = "text_longitude_key";
    String PREF_LATITUDE_KEY = "text_latitude_key";

    String PREF_NOTIFICATIONS_SWTICH = "switch_notifications";

    //Widgets Settings
    String PREF_ICON_LIBRARY_SWITCH = "switch_icon_library";
    String PREF_ICON_LIBRARY = "list_icon_library";
    String PREF_ICON_TYPE = "text_icon_type";
    String PREF_ICON_TYPE_PREFIX = "&#x";
    String PREF_ICON_TYPE_SUFFIX = ";";
    String PREF_CHART_SWITCH = "switch_chart";
    String PREF_CHART_TYPE = "list_chart";
    String PREF_MAP_SWITCH = "switch_map";

    //Runtime Values
    String VALUE_NONE = "NONE";
    String VALUE_NOT_APPLICABLE = "N/A";
    String VALUE_LAST_UPDATE_DATE = "lastUpdateDate";
    String VALUE_HISTORICAL_DATA_1 = "historicalData1";
    String VALUE_HISTORICAL_DATA_2 = "historicalData2";
    String VALUE_HISTORICAL_DATA_3 = "historicalData3";
    String VALUE_HISTORICAL_DATA_4 = "historicalData4";
    String VALUE_HISTORICAL_DATA_5 = "historicalData5";
    String VALUE_LONGITUDE = "longitudeValue";
    String VALUE_LATITUDE = "latitudeValue";
    String VALUE_DEVICE = "Device";
    int MAP_ZOOM_LEVEL = 13;
    String NOTIFICATIO_MESSAGE = "New Message Arrived";
}