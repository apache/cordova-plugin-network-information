<!---
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
-->

# org.apache.cordova.network-information

这个插件提供的旧版本的[网络信息 API][1]实现的。 它提供了有关该设备的移动电话和无线网络连接的信息和设备是否已连接到 internet。

 [1]: http://www.w3.org/TR/2011/WD-netinfo-api-20110607/

## 安装

    cordova plugin add org.apache.cordova.network-information
    

## 支持的平台

*   亚马逊火 OS
*   Android 系统
*   黑莓 10
*   iOS
*   Windows Phone 7 和 8
*   Tizen
*   Windows 8
*   火狐浏览器操作系统

# 连接

> `connection`对象，通过公开 `navigator.connection` ，提供了有关该设备的移动电话和无线网络连接的信息。

## 属性

*   connection.type

## 常量

*   Connection.UNKNOWN
*   Connection.ETHERNET
*   Connection.WIFI
*   Connection.CELL_2G
*   Connection.CELL_3G
*   Connection.CELL_4G
*   Connection.CELL
*   Connection.NONE

## connection.type

此属性提供快速的方法来确定设备的网络连接状态，和连接类型。

### 快速的示例

    function checkConnection() {
        var networkState = navigator.connection.type;
    
        var states = {};
        states[Connection.UNKNOWN]  = 'Unknown connection';
        states[Connection.ETHERNET] = 'Ethernet connection';
        states[Connection.WIFI]     = 'WiFi connection';
        states[Connection.CELL_2G]  = 'Cell 2G connection';
        states[Connection.CELL_3G]  = 'Cell 3G connection';
        states[Connection.CELL_4G]  = 'Cell 4G connection';
        states[Connection.CELL]     = 'Cell generic connection';
        states[Connection.NONE]     = 'No network connection';
    
        alert('Connection type: ' + states[networkState]);
    }
    
    checkConnection();
    

### API 更改

知道Cordova 2.3.0， `Connection` 对象是通过 `navigator.network.connection`被访问 ，之后才改为 `navigator.connection` 匹配的 W3C 规范。 它仍然是在其原来的位置，但已被废弃，最终将被删除。

### iOS 的怪癖

*   iOS 无法检测到蜂窝网络连接的类型。 
    *   `navigator.connection.type`设置为 `Connection.CELL` 为所有蜂窝数据。

### Windows Phone 怪癖

*   当运行在仿真器中，总能检测到 `navigator.connection.type` 作为`Connection.UNKNOWN`.

*   Windows Phone 不能检测的蜂窝网络连接的类型。
    
    *   `navigator.connection.type`设置为 `Connection.CELL` 为所有蜂窝数据。

### Tizen 怪癖

*   Tizen 只可以检测一个 WiFi 或者蜂窝连接。 
    *   `navigator.connection.type`设置为 `Connection.CELL_2G` 为所有蜂窝数据。

### 火狐浏览器操作系统的怪癖

*   火狐浏览器操作系统无法检测到蜂窝网络连接的类型。 
    *   `navigator.connection.type`设置为 `Connection.CELL` 为所有蜂窝数据。

# 与网络相关的事件

## offline

当一个应用程序离线，并且该设备未连接到互联网时，将触发该事件。

    document.addEventListener("offline", yourCallbackFunction, false);
    

### 详细信息

当以前连接的设备失去网络连接，以致应用程序不再可以访问互联网时，将触发该`offline`事件。 它依赖于连接API中相同的信息，并且 当 `connection.type`从 `NONE` 更改为其他任何值的时候，将触发该事件。

应用程序通常应使用 `document.addEventListener` 将一个事件侦听器附加一次 `deviceready` 事件火灾。

### 快速的示例

    document.addEventListener("offline", onOffline, false);
    
    function onOffline() {
        // Handle the offline event
    }
    

### iOS 的怪癖

在初始启动期间，第一次脱机事件 （如果适用） 至少需要一秒去触发。

### Windows Phone 7 的怪癖

当运行在仿真器中时， `connection.status` 始终是未知的，因此此事件将是*not*触发。

### Windows Phone 8 怪癖

仿真程序报告连接类型为 `Cellular` ，而不会更改，所以该事件将是*not*触发。

## online

当应用程序进入在线状态，并且该设备将成为连接到互联网时触发此事件。

    document.addEventListener("online", yourCallbackFunction, false);
    

### 详细信息

当以前未连接的移动设备接收到一个网络连接，以允许应用程序访问互联网时将触发该`online`事件。 它依赖于连接 API相同的信息，并且当 `connection.type` 变为`NONE`时才触发。.

应用程序通常应使用 `document.addEventListener` 将一个事件侦听器附加一次 `deviceready` 事件火灾。

### 快速的示例

    document.addEventListener("online", onOnline, false);
    
    function onOnline() {
        // Handle the online event
    }
    

### iOS 的怪癖

在初始启动期间，第一次至少需一秒钟来触发 `online` 事件 (如果适用)，在这之前`connection.type` 是`UNKNOWN`.

### Windows Phone 7 的怪癖

当运行在仿真器中， `connection.status` 始终是未知的因此此事件不会*不*火。

### Windows Phone 8 怪癖

仿真程序报告连接类型为 `Cellular` ，而不会更改，所以该事件将是*not*触发。