package com.volkswagenag.partnerlibrary.demomode;

import android.content.Context;
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
import java.util.List;
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

    private static final String DEMO_MODE_PROPERTY = "com.volkswagenag.parterapi.demomode";
    private static final String GET_PROP_LOCATION = "/system/bin/getprop";

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
     * @return boolean value of property {@link DemoModeUtils#DEMO_MODE_PROPERTY}
     */
    public static boolean isDemoModeEnabled() {
        try {
            //TODO: Try to find if the app can access os.SystemProperties directly to read the property.
            Process proc = Runtime.getRuntime().exec(new String[]{GET_PROP_LOCATION, DEMO_MODE_PROPERTY});
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            return Boolean.parseBoolean(reader.readLine());
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
    public static List<Integer> getIntegerList(JSONArray jsonArray) throws JSONException {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getInt(i));
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
}
