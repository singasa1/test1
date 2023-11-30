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
 // AppStoreUpdateContainer.aidl
package com.volkswagenag.ignite.appstore;

parcelable AppStoreUpdateContainer {
   String productId;
   String appType;
   String appName;
   String packageName;
   String versionName;
   long versionCode;
   String developerName;
   long availableSince;
   List<String> changeHistory;
}