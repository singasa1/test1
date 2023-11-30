/*
 * ********************************************************************
 *  COPYRIGHT (c) 2021 Harman International Industries, Inc.          *
 *                                                                    *
 *  All rights reserved                                               *
 *                                                                    *
 *  This software embodies materials and concepts which are           *
 *  confidential to Harman International Industries, Inc. and is      *
 *  made available solely pursuant to the terms of a written license  *
 *  agreement with Harman International Industries, Inc.              *
 *                                                                    *
 *  Designed and Developed by Harman International Industries, Inc.   *
 * -------------------------------------------------------------------*
 *  MODULE OR UNIT: IgniteAppStore                                    *
 * ********************************************************************
 */
// IAppStoreService.aidl
package com.volkswagenag.ignite.appstore;

import com.volkswagenag.ignite.appstore.IAppStoreUpdateListener;
/**
 * To get this AIDL you should bind com.harman.ignite.appstore/.services.AppStoreService
 */
interface IAppStoreService {

    /**
     * https://group-cip.audi.de/wiki/display/AHCP/AIDL+Compatibility+Guideline
     */
    const int VERSION = 1;

    /**
     * https://group-cip.audi.de/wiki/display/AHCP/AIDL+Compatibility+Guideline
     */
   int getIfcVersion() = /*slotNr*/ 0;

  /**
   * Client subscribes to get Updates for AppStore Apps or Services.
   *
   * @param appStoreUpdateListener Interface for callbacks.
   */
   oneway void getAvailableUpdates(in IAppStoreUpdateListener appStoreUpdateListener) = /*slotNr*/ 1;
}