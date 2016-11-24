/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova.networkinformation;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.text.format.Formatter;
import java.util.Locale;
import java.util.List;

public class NetworkManager extends CordovaPlugin {

    public static final int RECORD_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public static final int GPS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 4;
    public static final int GPS_COARSE_LOCATION_PERMISSION_REQUEST_CODE = 5;
    public static final int PHONE_CALL_PERMISSION_REQUEST_CODE = 6;
    public static final int SEND_SMS_PERMISSION_REQUEST_CODE = 7;

    public static int NOT_REACHABLE = 0;
    public static int REACHABLE_VIA_CARRIER_DATA_NETWORK = 1;
    public static int REACHABLE_VIA_WIFI_NETWORK = 2;

    public static final String WIFI = "wifi";
    public static final String WIMAX = "wimax";
    // mobile
    public static final String MOBILE = "mobile";

    // Android L calls this Cellular, because I have no idea!
    public static final String CELLULAR = "cellular";
    // 2G network types
    public static final String TWO_G = "2g";
    public static final String GSM = "gsm";
    public static final String GPRS = "gprs";
    public static final String EDGE = "edge";
    // 3G network types
    public static final String THREE_G = "3g";
    public static final String CDMA = "cdma";
    public static final String UMTS = "umts";
    public static final String HSPA = "hspa";
    public static final String HSUPA = "hsupa";
    public static final String HSDPA = "hsdpa";
    public static final String ONEXRTT = "1xrtt";
    public static final String EHRPD = "ehrpd";
    // 4G network types
    public static final String FOUR_G = "4g";
    public static final String LTE = "lte";
    public static final String UMB = "umb";
    public static final String HSPA_PLUS = "hspa+";
    // return type
    public static final String TYPE_UNKNOWN = "unknown";
    public static final String TYPE_ETHERNET = "ethernet";
    public static final String TYPE_ETHERNET_SHORT = "eth";
    public static final String TYPE_WIFI = "wifi";
    public static final String TYPE_2G = "2g";
    public static final String TYPE_3G = "3g";
    public static final String TYPE_4G = "4g";
    public static final String TYPE_NONE = "none";

    private enum CHANNEL {
        CHANNEL_UNKNOWN(0, 0),
        CHANNEL1_2400(1, 2412), CHANNEL2_2400(2, 2417), CHANNEL3_2400(3, 2422), CHANNEL4_2400(4, 2427), CHANNEL5_2400(5, 2432), CHANNEL6_2400(6, 2437), CHANNEL7_2400(7, 2442), CHANNEL8_2400(8, 2447), CHANNEL9_2400(9, 2452), CHANNEL10_2400(10, 2457), CHANNEL11_2400(11, 2462), CHANNEL12_2400(12, 2467), CHANNEL13_2400(13, 2472),CHANNEL14_2400(14, 2484),
        CHANNEL7_5000(7, 5035), CHANNEL8_5000(8, 5040), CHANNEL9_5000(9, 5045), CHANNEL11_5000(11, 5055), CHANNEL12_5000(12, 5060), CHANNEL16_5000(16, 5080), CHANNEL34_5000(34, 5170), CHANNEL36_5000(36, 5180), CHANNEL38_5000(38, 5190), CHANNEL40_5000(40, 5200), CHANNEL42_5000(42, 5210), CHANNEL44_5000(44, 5220), CHANNEL46_5000(46, 5230), CHANNEL48_5000(48, 5240), CHANNEL50_5000(50, 5250), CHANNEL52_5000(52, 5260), CHANNEL54_5000(54, 5270), CHANNEL56_5000(56, 5280), CHANNEL58_5000(58, 5290), CHANNEL60_5000(60, 5300), CHANNEL62_5000(62, 5310), CHANNEL64_5000(64, 5320), CHANNEL100_5000(100, 5500), CHANNEL102_5000(102, 5510), CHANNEL104_5000(104, 5520), CHANNEL106_5000(106, 5530), CHANNEL108_5000(108, 5540), CHANNEL110_5000(110, 5550), CHANNEL112_5000(112, 5560), CHANNEL114_5000(114, 5570), CHANNEL116_5000(116, 5580), CHANNEL118_5000(118, 5590), CHANNEL120_5000(120, 5600), CHANNEL122_5000(122, 5610), CHANNEL124_5000(124, 5620), CHANNEL126_5000(126, 5630), CHANNEL128_5000(128, 5640), CHANNEL132_5000(132, 5660), CHANNEL134_5000(134, 5670), CHANNEL136_5000(136, 5680), CHANNEL138_5000(138, 5690), CHANNEL140_5000(140, 5700), CHANNEL142_5000(142, 5710), CHANNEL144_5000(144, 5720), CHANNEL149_5000(149, 5745), CHANNEL151_5000(151, 5755), CHANNEL153_5000(153, 5765), CHANNEL155_5000(155, 5775), CHANNEL157_5000(157, 5785), CHANNEL159_5000(159, 5795), CHANNEL161_5000(161, 5805), CHANNEL165_5000(165, 5825), CHANNEL183_5000(183, 4915), CHANNEL184_5000(184, 4920), CHANNEL185_5000(185, 4925), CHANNEL187_5000(187, 4935), CHANNEL188_5000(188, 4940), CHANNEL189_5000(189, 4945), CHANNEL192_5000(192, 4960), CHANNEL196_5000(196, 4980),
        ;
        private int channel;
        private int frequency;
        private String type;
        private CHANNEL(int channel, int frequency) {
            this.channel = channel;
            this.frequency = frequency;
            if (frequency >= 2412 && frequency <= 2484) {
                this.type = "2.4GHz";
            } else if (frequency > 3657 && frequency < 3693) {
                this.type = "3.65GHz";
            } else if (frequency >= 4910 && frequency <= 5835) {
                this.type = "5.0GHz";
            }
        };
    }

    private static final String LOG_TAG = "NetworkManager";

    private CallbackContext connectionCallbackContext;

    ConnectivityManager sockMan;
    WifiManager wifiManager;
    BroadcastReceiver receiver;
    private JSONObject lastInfo = null;

    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        if (!this.cordova.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
        {
            this.cordova.requestPermissions(this, GPS_COARSE_LOCATION_PERMISSION_REQUEST_CODE,  new String[] {Manifest.permission.ACCESS_COARSE_LOCATION});
        }

        this.sockMan = (ConnectivityManager) cordova.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        this.wifiManager = (WifiManager) cordova.getActivity().getSystemService(Context.WIFI_SERVICE);
        this.connectionCallbackContext = null;

        // We need to listen to connectivity events to update navigator.connection
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (this.receiver == null) {
            this.receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // (The null check is for the ARM Emulator, please use Intel Emulator for better results)
                    if(NetworkManager.this.webView != null)
                        updateConnectionInfo(sockMan.getActiveNetworkInfo());
                }
            };
            webView.getContext().registerReceiver(this.receiver, intentFilter);
        }

    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false otherwise.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("getConnectionInfo")) {
            this.connectionCallbackContext = callbackContext;
            NetworkInfo nwi = sockMan.getActiveNetworkInfo();
            JSONObject thisInfo = this.getConnectionInfo(nwi);

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, thisInfo);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            return true;
        }
        return false;
    }

    /**
     * Stop network receiver.
     */
    public void onDestroy() {
        if (this.receiver != null) {
            try {
                webView.getContext().unregisterReceiver(this.receiver);
            } catch (Exception e) {
                LOG.e(LOG_TAG, "Error unregistering network receiver: " + e.getMessage(), e);
            } finally {
                receiver = null;
            }
        }
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------

    /**
     * Updates the JavaScript side whenever the connection changes
     *
     * @param info the current active network info
     * @return
     */
    private void updateConnectionInfo(NetworkInfo nwi) {
        // send update to javascript "navigator.network.connection"
        // Jellybean sends its own info
        JSONObject thisInfo = this.getConnectionInfo(nwi);
        if(!thisInfo.equals(lastInfo))
        {
            String connectionType = "";
            JSONObject wifiInfo = null;
            JSONArray wifiScan = null;
            try {
                connectionType = thisInfo.get("type").toString();
                wifiInfo = (JSONObject) thisInfo.get("wifi");
                wifiScan = (JSONArray) thisInfo.get("scan");
            } catch (JSONException e) {
                LOG.d(LOG_TAG, e.getLocalizedMessage());
            }

            sendUpdate(connectionType, wifiInfo, wifiScan);
            lastInfo = thisInfo;
        }
    }

    /**
     * Get the latest network connection information
     *
     * @param info the current active network info
     * @return a JSONObject that represents the network info
     */
    private JSONObject getConnectionInfo(NetworkInfo nwi) {
        String type = TYPE_NONE;
        String extraInfo = "";
        if (nwi != null) {
            // If we are not connected to any network set type to none
            if (!nwi.isConnected()) {
                type = TYPE_NONE;
            }
            else {
                type = getType(nwi);
            }
            extraInfo = nwi.getExtraInfo();
        }

        JSONObject wifiInfo = new JSONObject();
        JSONArray scanInfoList = new JSONArray();
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            try {
                WifiInfo wfi = wifiManager.getConnectionInfo();

                wifiInfo
                .put("bssid", wfi.getBSSID())
                .put("ssid", wfi.getSSID())
                .put("ipaddress", Formatter.formatIpAddress(wfi.getIpAddress()))
                .put("macaddress", wfi.getMacAddress())
                .put("networkid", wfi.getNetworkId())
                .put("rssi", wfi.getRssi())
                .put("level", WifiManager.calculateSignalLevel(wfi.getRssi(), 5))
                .put("frequency", wfi.getFrequency())
                .put("linkspeed", wfi.getLinkSpeed());
            } catch (JSONException e) {
                LOG.e(LOG_TAG, e.getLocalizedMessage());
            }

            try {
                LOG.i(LOG_TAG, "[startScan]");
                // start scan
                wifiManager.startScan();
                LOG.i(LOG_TAG, "[getScan]");
                // get scan result
                List<ScanResult> wfsResults = wifiManager.getScanResults();
                LOG.i(LOG_TAG, "[getScanResult] " + wfsResults.size());
                for (int i = 0; i < wfsResults.size(); i++) {
                    ScanResult wfs = wfsResults.get(i);

                    JSONObject scanInfo = new JSONObject();
                    CHANNEL c = getChannel(wfs.frequency);
                    scanInfo
                    .put("bssid", wfs.BSSID)
                    .put("ssid", wfs.SSID)
                    .put("capabilities", wfs.capabilities)
                    .put("frequency", wfs.frequency)
                    .put("channel", c.channel)
                    .put("type", c.type)
                    .put("level", wfs.level);

                    scanInfoList.put(scanInfo);
                }
              } catch (JSONException e) {
                  LOG.e(LOG_TAG, e.getLocalizedMessage());
              }
        }

        LOG.d(LOG_TAG, "Connection Type: " + type);
        LOG.d(LOG_TAG, "Connection Extra Info: " + extraInfo);

        JSONObject connectionInfo = new JSONObject();

        try {
            connectionInfo.put("type", type);
            connectionInfo.put("extraInfo", extraInfo);
            connectionInfo.put("wifi", wifiInfo);
            connectionInfo.put("scan", scanInfoList);
        } catch (JSONException e) {
            LOG.d(LOG_TAG, e.getLocalizedMessage());
        }

        return connectionInfo;
    }

    /**
     * Create a new plugin result and send it back to JavaScript
     *
     * @param connection the network info to set as navigator.connection
     */
    private void sendUpdate(String type, JSONObject wifi, JSONArray scan) {
        if (connectionCallbackContext != null) {
            JSONObject jsono = new JSONObject();
            try {
                jsono.put("type", type);
                jsono.put("wifi", wifi);
                jsono.put("scan", scan);
            } catch(JSONException e) {

            }
            PluginResult result = new PluginResult(PluginResult.Status.OK, jsono);
            result.setKeepCallback(true);
            connectionCallbackContext.sendPluginResult(result);
        }
        webView.postMessage("networkconnection", type);
    }

    /**
     * Determine the type of connection
     *
     * @param info the network info so we can determine connection type.
     * @return the type of mobile network we are on
     */
    private String getType(NetworkInfo info) {
        if (info != null) {
            String type = info.getTypeName().toLowerCase(Locale.US);

            LOG.d(LOG_TAG, "toLower : " + type.toLowerCase());
            LOG.d(LOG_TAG, "wifi : " + WIFI);
            if (type.equals(WIFI)) {
                return TYPE_WIFI;
            }
            else if (type.toLowerCase().equals(TYPE_ETHERNET) || type.toLowerCase().startsWith(TYPE_ETHERNET_SHORT)) {
                return TYPE_ETHERNET;
            }
            else if (type.equals(MOBILE) || type.equals(CELLULAR)) {
                type = info.getSubtypeName().toLowerCase(Locale.US);
                if (type.equals(GSM) ||
                        type.equals(GPRS) ||
                        type.equals(EDGE) ||
                        type.equals(TWO_G)) {
                    return TYPE_2G;
                }
                else if (type.startsWith(CDMA) ||
                        type.equals(UMTS) ||
                        type.equals(ONEXRTT) ||
                        type.equals(EHRPD) ||
                        type.equals(HSUPA) ||
                        type.equals(HSDPA) ||
                        type.equals(HSPA) ||
                        type.equals(THREE_G)) {
                    return TYPE_3G;
                }
                else if (type.equals(LTE) ||
                        type.equals(UMB) ||
                        type.equals(HSPA_PLUS) ||
                        type.equals(FOUR_G)) {
                    return TYPE_4G;
                }
            }
        }
        else {
            return TYPE_NONE;
        }
        return TYPE_UNKNOWN;
    }

    /**
     * Determine the channel of connection
     *
     * @param frequency the network info so we can determine connection channel.
     * @return the channel of mobile network we are on
     */
    private CHANNEL getChannel(int frequency) {
      for (CHANNEL c : CHANNEL.values()) {
          if (c.frequency == frequency) {
              return c;
          }
      }
      return CHANNEL.CHANNEL_UNKNOWN;
    }

}
