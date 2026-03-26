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
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

#import "CDVConnection.h"
#import "CDVReachability.h"

@interface CDVConnection (PrivateMethods)
- (void)updateOnlineStatus;
- (void)sendPluginResult;
@end

@implementation CDVConnection

@synthesize connectionType, internetReach;

- (void)getConnectionInfo:(CDVInvokedUrlCommand*)command
{
    _callbackId = command.callbackId;
    [self sendPluginResult];
}

- (void)sendPluginResult
{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:self.connectionType];

    [result setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:result callbackId:_callbackId];
}

- (NSString*)w3cConnectionTypeFor:(CDVReachability*)reachability
{
    NetworkStatus networkStatus = [reachability currentReachabilityStatus];

    switch (networkStatus) {
        case NotReachable:
            return @"none";

        case ReachableViaWWAN:
        {
            BOOL isConnectionRequired = [reachability connectionRequired];
            if (isConnectionRequired) {
                return @"none";
            } else {
                if ([[[UIDevice currentDevice] systemVersion] compare:@"7.0" options:NSNumericSearch] != NSOrderedAscending) {
                    NSString* radioAccessTechnology = [self currentRadioAccessTechnology];
                    if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyGPRS]) {
                        return @"2g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyEdge]) {
                        return @"2g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyWCDMA]) {
                        return @"3g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyHSDPA]) {
                        return @"3g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyHSUPA]) {
                        return @"3g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyCDMA1x]) {
                        return @"3g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyCDMAEVDORev0]) {
                        return @"3g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyCDMAEVDORevA]) {
                        return @"3g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyCDMAEVDORevB]) {
                        return @"3g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyeHRPD]) {
                        return @"3g";
                    } else if ([radioAccessTechnology isEqualToString:CTRadioAccessTechnologyLTE]) {
                        return @"4g";
                    }
                }
                return @"cellular";
            }
        }
        case ReachableViaWiFi:
        {
            BOOL isConnectionRequired = [reachability connectionRequired];
            if (isConnectionRequired) {
                return @"none";
            } else {
                return @"wifi";
            }
        }
        default:
            return @"unknown";
    }
}

- (NSString*)currentRadioAccessTechnology
{
    CTTelephonyNetworkInfo *telephonyInfo = [CTTelephonyNetworkInfo new];

    if (@available(iOS 12.0, *)) {
        // iOS 12+ exposes Radio Access Technology per cellular service (multi-SIM capable)
        NSDictionary<NSString*, NSString*> *serviceCurrentRadioAccessTechnology = telephonyInfo.serviceCurrentRadioAccessTechnology;
        if (serviceCurrentRadioAccessTechnology.count == 0) {
            return nil;
        }

        NSString *currentRadioAccessTechnology = nil;
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= 130000
        if (@available(iOS 13.0, *)) {
            // Prefer the Radio Access Technology for the currently active data service when available.
            // This is the most likely to reflect the actual network type used for data connectivity.
            NSString *dataServiceIdentifier = telephonyInfo.dataServiceIdentifier;

            if (dataServiceIdentifier.length > 0) {
                currentRadioAccessTechnology = serviceCurrentRadioAccessTechnology[dataServiceIdentifier];
            }
        }
#endif
        if (!currentRadioAccessTechnology) {
            // Deterministic fallback for iOS 12 (or missing data service identifier)
            // Get the Radio Access Technology for the first available cellular service, sorted by service identifier.
            NSArray<NSString*> *sortedKeys = [[serviceCurrentRadioAccessTechnology allKeys] sortedArrayUsingSelector:@selector(compare:)];
            NSString *firstKey = sortedKeys.firstObject;
            currentRadioAccessTechnology = firstKey != nil ? serviceCurrentRadioAccessTechnology[firstKey] : nil;
        }

        return currentRadioAccessTechnology;
    }

// Fallback for iOS 11 and earlier, which only supports a single Radio Access Technology.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    return telephonyInfo.currentRadioAccessTechnology;
#pragma clang diagnostic pop
}

- (BOOL)isCellularConnection:(NSString*)theConnectionType
{
    return [theConnectionType isEqualToString:@"2g"] ||
           [theConnectionType isEqualToString:@"3g"] ||
           [theConnectionType isEqualToString:@"4g"] ||
           [theConnectionType isEqualToString:@"cellular"];
}

- (void)updateReachability:(CDVReachability*)reachability
{
    if (reachability) {
        // check whether the connection type has changed
        NSString* newConnectionType = [self w3cConnectionTypeFor:reachability];
        if ([newConnectionType isEqualToString:self.connectionType]) { // the same as before, remove dupes
            return;
        } else {
            self.connectionType = [self w3cConnectionTypeFor:reachability];
        }
    }
    [self sendPluginResult];
}

- (void)updateConnectionType:(NSNotification*)note
{
    CDVReachability* curReach = [note object];

    if ((curReach != nil) && [curReach isKindOfClass:[CDVReachability class]]) {
        [self updateReachability:curReach];
    }
}

- (void)onPause
{
    [self.internetReach stopNotifier];
}

- (void)onResume
{
    [self.internetReach startNotifier];
    [self updateReachability:self.internetReach];
}

- (void)pluginInitialize
{
    self.connectionType = @"none";
    self.internetReach = [CDVReachability reachabilityForInternetConnection];
    self.connectionType = [self w3cConnectionTypeFor:self.internetReach];
    [self.internetReach startNotifier];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateConnectionType:)
                                                 name:kReachabilityChangedNotification object:nil];
    if (@available(iOS 12.0, *)) {
        // iOS 12+ replacement for CTRadioAccessTechnologyDidChangeNotification.
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateConnectionType:)
                                                     name:CTServiceRadioAccessTechnologyDidChangeNotification object:nil];
    } else {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        // iOS 11 fallback to keep backward compatibility.
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateConnectionType:)
                                                     name:CTRadioAccessTechnologyDidChangeNotification object:nil];
#pragma clang diagnostic pop
    }
    if (UIApplicationDidEnterBackgroundNotification && UIApplicationWillEnterForegroundNotification) {
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPause) name:UIApplicationDidEnterBackgroundNotification object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onResume) name:UIApplicationWillEnterForegroundNotification object:nil];
    }
}

@end
