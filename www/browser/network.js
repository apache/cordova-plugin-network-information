/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/

/*global module, require*/

var cordova = require('cordova'),
    Connection = require('./Connection');

var DOCUMENT_EVENTS_CHECK_INTERVAL = 500; // ms
// Flag that indicates that ew need to re-fire online/offline events at document level
// (Workaround for Chrome, since it fires such events only for window object)
var NEED_FIRE_DOCUMENT_EVENT_MANUALLY = false;

function NetworkConnection() {
    this.type = Connection.UNKNOWN;
}

/**
 * Get connection info
 *
 * @param {Function} successCallback The function to call when the Connection data is available
 */
NetworkConnection.prototype.getInfo = function(successCallback) {
    successCallback(this.type);
};

Object.defineProperty(NetworkConnection.prototype, 'type', {
    get: function () {
        // It is not possible to determine real connection type in browser
        // so we always report Connection.UNKNOWN when online
        return (window.navigator.onLine === false ? Connection.NONE : Connection.UNKNOWN);
    },
    configurable: true,
    enumerable: true
});

// This function tries to detect if document online/offline events is being fired
// after corresponding window events, and if not, then fires them manually
// This is workaround for Chrome, which fires only window online/offline events
// and regarding to plugin spec we need these events at document object
var eventRedirectHandler = function (e) {
    // NEED_FIRE_DOCUMENT_EVENT_MANUALLY flag is already set,
    // just fire corresponding document event and return
    if (NEED_FIRE_DOCUMENT_EVENT_MANUALLY) {
        cordova.fireDocumentEvent(e.type);
        return;
    }

    // Flag that indicates whether corresponding document even is fired
    var documentStateEventFired = false;
    var setDocumentStateEventFired = function() {
        documentStateEventFired = true;
    };
    document.addEventListener(e.type, setDocumentStateEventFired);
    setTimeout(function () {
        // Remove unnecessary listener
        document.removeEventListener(e.type, setDocumentStateEventFired);
        // if document event hasn't been fired in specified interval (500 ms by default),
        // then we're in chrome and need to fire it manually
        if (!documentStateEventFired) {
            NEED_FIRE_DOCUMENT_EVENT_MANUALLY = true;
            cordova.fireDocumentEvent(e.type);
        }
    }, DOCUMENT_EVENTS_CHECK_INTERVAL);
};

// Subscribe to native online/offline events
window.addEventListener('online', eventRedirectHandler);
window.addEventListener('offline', eventRedirectHandler);

var me = new NetworkConnection();

require("cordova/exec/proxy").add("NetworkStatus", { getConnectionInfo: me.getConnectionInfo });

module.exports = me;
