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
// ErrorCodes.aidl
package com.volkswagenag.ignite.appstore;

// ATTENTION: Support for Enums with AIDL-Tool > Android SDK-Build-Tools 29
@Backing(type="int")
enum ErrorCodes {
    ERROR_REASON_NONE,
    ERROR_REASON_NOT_SYNCED,
    ERROR_REASON_NO_NETWORK,
    ERROR_REASON_NO_DATA
}