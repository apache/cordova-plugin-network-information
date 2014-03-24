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
                return @"cellular";
            }
        }
        case ReachableViaWiFi:
            return @"wifi";

        default:
            return @"unknown";
    }
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

- (CDVPlugin*)initWithWebView:(UIWebView*)theWebView
{
    self = [super initWithWebView:theWebView];
    if (self) {
        self.connectionType = @"none";
        self.internetReach = [CDVReachability reachabilityForInternetConnection];
        self.connectionType = [self w3cConnectionTypeFor:self.internetReach];
        [self.internetReach startNotifier];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateConnectionType:)
                                                     name:kReachabilityChangedNotification object:nil];
        if (&UIApplicationDidEnterBackgroundNotification && &UIApplicationWillEnterForegroundNotification) {
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPause) name:UIApplicationDidEnterBackgroundNotification object:nil];
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onResume) name:UIApplicationWillEnterForegroundNotification object:nil];
        }
    }
    return self;
}

@end
