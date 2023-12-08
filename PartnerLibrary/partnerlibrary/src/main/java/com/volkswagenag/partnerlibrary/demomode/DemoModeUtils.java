/*
 * CONFIDENTIAL CARIAD Estonia AS
 *
 * (c) 2023 CARIAD Estonia AS, All rights reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of CARIAD Estonia AS (registry code 14945253).
 * The intellectual and technical concepts contained herein are proprietary to CARIAD Estonia AS. and may be covered by
 * patents, patents in process, and are protected by trade secret or copyright law.
 * Usage or dissemination of this information or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from CARIAD Estonia AS.
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code,
 * which includes information that is confidential and/or proprietary, and is a trade secret of CARIAD Estonia AS.
 * Any reproduction, modification, distribution, public performance, or public display of or through use of this source
 * code without the prior written consent of CARIAD Estonia AS is strictly prohibited and in violation of applicable
 * laws and international treaties. The receipt or possession of this source code and/ or related information does not
 * convey or imply any rights to reproduce, disclose or distribute its contents or to manufacture, use or sell anything
 * that it may describe in whole or in part.
 */
package com.volkswagenag.partnerlibrary.demomode;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DemoModeUtils {

    /**
     * Converter interface that is used to convert Strings obtained from JSON to the object T
     */
    public interface Converter<T> {
        /**
         * convert the string to Object T
         * @param str str to be converted
         * @return T converted object T
         */
        public T convert(String str);
    }

    private static final String TAG = DemoModeUtils.class.getSimpleName();

    private static final String DEMO_MODE_PROPERTY = "volkswagenag.parterapi.demomode";
    private static final String PERSIST_DEMO_MODE_PROPERTY = "persist.volkswagenag.parterapi.demomode";
    private static final String GET_PROP_LOCATION = "/system/bin/getprop";

    private static final String CARIAD_VWAE_RESTRICTED_NAMESPACE = "com.volkswagenag.restricted.";

    /**
     * Read JSONObject from the file specified by fileName. The should be located in the context's
     * external Directory /sdcard/Android/data/{app-package-name}/files/
     * Note: app-package-name is the package name of the app this library will be included in.
     * @param context
     * @param fileName fileName of the file present in /sdcard/Android/data/{app-package-name}/files/
     * @return JSONObject of the contents obtained from the file
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject readFromFile(Context context, String fileName) throws IOException, JSONException {
        File file = new File(context.getExternalFilesDir(null), fileName);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null){
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();

        return new JSONObject(stringBuilder.toString());
    }

    /**
     * Returns the property {@link DemoModeUtils#DEMO_MODE_PROPERTY} value in boolean.
     * @return boolean true if {@link DemoModeUtils#DEMO_MODE_PROPERTY} or
     *                      {@link DemoModeUtils#PERSIST_DEMO_MODE_PROPERTY} are enabled
     *                 false otherwise
     */
    public static boolean isDemoModeEnabled() {
        try {
            //TODO: Try to find if the app can access os.SystemProperties directly to read the property.
            Process getDemoModeProp = Runtime.getRuntime().exec(new String[]{GET_PROP_LOCATION, DEMO_MODE_PROPERTY});
            BufferedReader reader = new BufferedReader(new InputStreamReader(getDemoModeProp.getInputStream()));
            boolean demoModeProp = Boolean.parseBoolean(reader.readLine());
            Log.d(TAG, "property " + DEMO_MODE_PROPERTY + " value: " + demoModeProp);

            getDemoModeProp = Runtime.getRuntime().exec(new String[]{GET_PROP_LOCATION, PERSIST_DEMO_MODE_PROPERTY});
            reader = new BufferedReader(new InputStreamReader(getDemoModeProp.getInputStream()));
            boolean persistDemoModeProp = Boolean.parseBoolean(reader.readLine());
            Log.d(TAG, "property " + PERSIST_DEMO_MODE_PROPERTY + " value: " + persistDemoModeProp);

            return demoModeProp || persistDemoModeProp;
        } catch (IOException e) {
            Log.d(TAG, "Failure reading demo mode property: " + DEMO_MODE_PROPERTY);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Converts JSONArray to Integer list
     * @param jsonArray
     * @return
     * @throws JSONException
     */
    public static List<Float> getFloatList(JSONArray jsonArray) throws JSONException {
        List<Float> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add((float) jsonArray.getDouble(i));
        }
        return list;
    }

    /**
     * Converts JSONArray to List of object T
     * @param jsonArray
     * @param converter to use to convert String to T
     * @return List<T>
     * @param <T>
     * @throws JSONException
     */
    public static <T> List<T> getConvertedList(JSONArray jsonArray, Converter<T> converter) throws JSONException {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(converter.convert(jsonArray.getString(i)));
        }
        return list;
    }

    public static Set<String> getFilteredPermissionList(Context context) {
        Set<String> filteredPermissions = new HashSet<>();
        try {
            PackageInfo packageInfo = context
                    .getPackageManager()
                    .getPackageInfo(
                            context.getPackageName(),
                            PackageManager.GET_PERMISSIONS);

            for (String permission : packageInfo.requestedPermissions) {
                Log.d(TAG, "permission Name: " + permission);
                if (permission.startsWith(CARIAD_VWAE_RESTRICTED_NAMESPACE)) {
                    filteredPermissions.add(permission);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        return filteredPermissions;
    }
}
