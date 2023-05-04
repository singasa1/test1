package technology.cariad.partnerverifierlibrary;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private static final String restrictedNameSpace = "technology.cariad.vwae.restricted.";
    private static final String partnerEnablerServicePackageName = "technology.cariad.partnerenablerservice";

    public static boolean verify(Context context, String packageName, String metadata) {
        boolean res = false;

        PackageManager pm = context.getPackageManager();
        int flags = PackageManager.GET_PERMISSIONS;
        PackageInfo packageInfo = null;
        PackageInfo enablerPackageInfo = null;

        try {
            // Get the packageInfo for the given package name(3rd party app package info)
            packageInfo = pm.getPackageInfo(packageName, flags);
            // Get the packageInfo for the partner enabler service package name
            enablerPackageInfo = pm.getPackageInfo(partnerEnablerServicePackageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // get the request permission for the given package name(3rd part app package info)
        String[] permissionList = packageInfo.requestedPermissions;

        // Read the permissions defined in PartnerEnablerService AndroidManifest.xml file and create the permission list.
        PermissionInfo[] enablerPermissionInfoList = enablerPackageInfo.permissions;

        int length = enablerPermissionInfoList.length;
        String[] enablerPermissionList = new String[length];
        int i = 0;
        for(PermissionInfo info: enablerPermissionInfoList) {
            Log.d(TAG,"Defined Permission: " + info.name);
            enablerPermissionList[i++] = info.name;
        }
        // Form the meta-data string to be verified
        String dataString = packageName + ";";
        for (String permission : permissionList) {
            Log.d(TAG, "permission Name: " + permission);
            if (permission.startsWith(restrictedNameSpace)) {
                if (Arrays.stream(enablerPermissionList).anyMatch(permission::equals)) {
                    dataString += permission + ";";
                } else {
                    Log.d(TAG,"Given permission is not defined in the EnablerService");
                    return false;
                }
            }
        }
        Log.d(TAG, "String to be verified: " + dataString);

        try {
            byte[] dataBytes = dataString.getBytes(); // TODO: encoding!
            Base64.Decoder mimeDecoder = Base64.getMimeDecoder();
            Signature sig = Signature.getInstance("SHA256WithRSA");
            PublicKey pubKey = getPublicKey(context);
//            Log.d(TAG,"PubKey: " + pubKey);
            sig.initVerify(pubKey);
            byte[] metadataByte = mimeDecoder.decode(metadata);
            Log.d(TAG,"Metadata value String: " + metadata);
            Log.d(TAG,"Metadata value Bytes: " + new String(metadataByte));
            Log.d(TAG,"Metadata value Bytes: " + metadataByte);
            sig.update(dataBytes);
            res = sig.verify(metadataByte);
            Log.d(TAG,"Verification result: " + res);
        }
        catch (Exception e) {
                e.printStackTrace();
        }
        return res;
    }

    private static PublicKey getPublicKey(Context context) {
        PublicKey result = null;

        try {
            StringBuilder sb = new StringBuilder();
            String tempKey = null;
            InputStream is = null;
            try {
                is = context.getAssets().open("public_key.pem");
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8 ));

                while (true) {
                    if (!((tempKey = br.readLine()) != null)) break;
                    sb.append(tempKey);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG,"tempKey: " + sb.toString());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Base64.Decoder mimeDecoder = Base64.getMimeDecoder();
            String publicKeyPEM = sb.toString().replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll(System.getProperty("line.separator"), "")
                    .replace("-----END PUBLIC KEY-----", "");
            Log.d(TAG,"publicKeyPEM: " + publicKeyPEM);
            byte[] encodedPubKey = mimeDecoder.decode(publicKeyPEM);

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPubKey);
            result = keyFactory.generatePublic(publicKeySpec);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
