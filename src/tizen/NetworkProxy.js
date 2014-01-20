var cordova = require('cordova');
var Connection = require('./Connection');

module.exports = {
    getConnectionInfo: function(successCallback, errorCallback) {
        var cncType = Connection.NONE;
        var infoCount = 0;
        var deviceCapabilities = null;
        var timerId = 0;
        var timeout = 300;


        function connectionCB() {
            if (timerId !== null) {
                clearTimeout(timerId);
                timerId = null;
            }

            infoCount++;

            if (infoCount > 1) {
                if (successCallback) {
                    successCallback(cncType);
                }
            }
        }

        function errorCB(error) {
            console.log("Error: " + error.code + "," + error.name + "," + error.message);

            if (errorCallback) {
                errorCallback();
            }
        }

        function wifiSuccessCB(wifi) {
            if ((wifi.status === "ON")  && (wifi.ipAddress.length !== 0)) {
                cncType = Connection.WIFI;
            }
            connectionCB();
        }

        function cellularSuccessCB(cell) {
            if ((cncType === Connection.NONE) && (cell.status === "ON") && (cell.ipAddress.length !== 0)) {
                cncType = Connection.CELL_2G;
            }
            connectionCB();
        }


        deviceCapabilities = tizen.systeminfo.getCapabilities();


        timerId = setTimeout(function() {
            timerId = null;
            infoCount = 1;
            connectionCB();
        }, timeout);


        if (deviceCapabilities.wifi) {
            tizen.systeminfo.getPropertyValue("WIFI_NETWORK", wifiSuccessCB, errorCB);
        }

        if (deviceCapabilities.telephony) {
            tizen.systeminfo.getPropertyValue("CELLULAR_NETWORK", cellularSuccessCB, errorCB);
        }
    }
};

require("cordova/tizen/commandProxy").add("NetworkStatus", module.exports);
