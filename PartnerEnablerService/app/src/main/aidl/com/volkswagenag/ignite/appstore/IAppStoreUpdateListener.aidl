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
// IAppStoreUpdateListener.aidl
package com.volkswagenag.ignite.appstore;

import com.volkswagenag.ignite.appstore.AppStoreUpdateContainer;
import com.volkswagenag.ignite.appstore.ErrorCodes;

interface IAppStoreUpdateListener {
  oneway void getUpdateCallback(in List<AppStoreUpdateContainer> updates, in ErrorCodes errorCode);
}
