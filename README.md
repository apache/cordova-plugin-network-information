---
title: Network Information
description: Get information about wireless connectivity.
---
<!--
# license: Licensed to the Apache Software Foundation (ASF) under one
#         or more contributor license agreements.  See the NOTICE file
#         distributed with this work for additional information
#         regarding copyright ownership.  The ASF licenses this file
#         to you under the Apache License, Version 2.0 (the
#         "License"); you may not use this file except in compliance
#         with the License.  You may obtain a copy of the License at
#
#           http://www.apache.org/licenses/LICENSE-2.0
#
#         Unless required by applicable law or agreed to in writing,
#         software distributed under the License is distributed on an
#         "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#         KIND, either express or implied.  See the License for the
#         specific language governing permissions and limitations
#         under the License.
-->

# cordova-plugin-network-information


This plugin provides an implementation of an old version of the
[Network Information API](http://www.w3.org/TR/2011/WD-netinfo-api-20110607/).
It provides information about the device's cellular and
wifi connection, and whether the device has an internet connection.

> To get a few ideas how to use the plugin, check out the [sample](#sample) at the bottom of this page or go straight to the [reference](#reference) content.

Report issues with this plugin on the [Apache Cordova issue tracker][Apache Cordova issue tracker].

##<a name="reference"></a>Reference

## Installation

    cordova plugin add cordova-plugin-network-information

## Supported Platforms

- Android

# Connection

> The `connection` object, exposed via `navigator.connection`,  provides information about the device's cellular and wifi connection.

## Properties

- connection.type
- connection.wifi
- connection.scan

## Constants

- Connection.UNKNOWN
- Connection.ETHERNET
- Connection.WIFI
- Connection.CELL_2G
- Connection.CELL_3G
- Connection.CELL_4G
- Connection.CELL
- Connection.NONE

## connection.type

This property offers a fast way to determine the device's network
connection state, and type of connection.

## connection.wifi

Information on the connected Wifi.
It can not be acquired if it is not connected.

## connection.scan

Information on the surrounding AP wifi.

### Quick Example

```javascript
var app = {
    initialize: function() {
        this.bindEvents();
    },
    bindEvents: function() {
        document.addEventListener("online", this.onOnline, false);
        document.addEventListener("offline", this.onOffline, false);
    },
    onOnline: function() {
        app.writeWifiInfo();
    },
    onOffline: function() {
      console.log("lost connection");
    },
    writeWifiInfo : function() {
      var networkState = navigator.connection.type;
      console.log('Connection type: ' + networkState);

      if (navigator.connection.wifi) {
        var bssid = navigator.connection.wifi.bssid;
        var ssid = navigator.connection.wifi.ssid;
        var ipaddress = navigator.connection.wifi.ipaddress;
        var macaddress = navigator.connection.wifi.macaddress;
        var networkid = navigator.connection.wifi.networkid;
        var rssi = navigator.connection.wifi.rssi;
        var level = navigator.connection.wifi.level;
        var linkspeed = navigator.connection.wifi.linkspeed;

        console.log('[Connecting Wifi]bssid: ' + bssid + ', '
        + 'ssid: ' + ssid + ', '
        + 'ipaddress: ' + ipaddress + ', '
        + 'macaddress: ' + macaddress + ', '
        + 'networkid: ' + networkid + ', '
        + 'rssi: ' + rssi + ', '
        + 'level: ' + level + ', '
        + 'linkspeed: ' + linkspeed
        );
      }
      if (navigator.connection.scan) {
        var scan = navigator.connection.scan;
        for (var idx in scan) {
          var bssid = scan[idx].bssid;
          var ssid = scan[idx].ssid;
          var capabilities = scan[idx].capabilities;
          var frequency = scan[idx].frequency;
          var level = scan[idx].level;
          var type = scan[idx].type;
          var channel = scan[idx].channel;

          console.log('[Scan]bssid: ' + bssid + ', '
          + 'ssid: ' + ssid + ', '
          + 'frequency: ' + frequency + 'MHz, '
          + 'level: ' + level + 'dBm'
          + 'capabilities: ' + capabilities
          + 'channel: ' + channel
          );
        }
      }
    }
}
app.initialize();
```
### API Change

Until Cordova 2.3.0, the `Connection` object was accessed via
`navigator.network.connection`, after which it was changed to
`navigator.connection` to match the W3C specification.  It's still
available at its original location, but is deprecated and will
eventually be removed.
