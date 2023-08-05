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
package technology.cariad.partnerenablerservice.verifierservice;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.SigningInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private static final String CARIAD_VWAE_RESTRICTED_NAMESPACE = "technology.cariad.vwae.restricted.";
    private static final String PARTNER_ENABLER_SERVICE_PACKAGE_NAME = "technology.cariad.partnerenablerservice";

    /**
     * This method verifies the app digital signature.
     *
     * @param context context of the verifier library service
     * @param packageName package name of the app that uses the PartnerLibrary
     * @param metadata metadata of the app from its manifest file/packagemanager
     * @return  returns true if verification succeeds. false if verification fails.
     */
    public static boolean verify(Context context, String packageName, String metadata) {
        boolean res = false;

        if (context == null || packageName == null || packageName.isEmpty() ||
                metadata == null || metadata.isEmpty()) {
            return res;
        }

        PackageManager pm = context.getPackageManager();
        int flags = PackageManager.GET_PERMISSIONS | PackageManager.GET_SIGNING_CERTIFICATES;
        PackageInfo packageInfo = null;

        try {
            // Get the packageInfo for the given package name(3rd party app package info)
            packageInfo =
                    context
                            .getPackageManager()
                            .getPackageInfo(
                                    packageName,
                                    PackageManager.GET_PERMISSIONS | PackageManager.GET_SIGNING_CERTIFICATES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return res;
        }

        String dataString = createMetaDataString(packageInfo, packageName);
        if (dataString == null || dataString.isEmpty()) {
            return res;
        }
        Log.d(TAG, "String to be verified: " + dataString);

        PublicKey pubKey = getPublicKey(context);
        if (pubKey == null) {
            Log.e(TAG,"Public Key is null");
            return res;
        }

        res = verifySignature(pubKey, metadata, dataString);
        return res;
    }

    /**
     * This method creates the data string to be verified.
     *
     * @param context context of the caller
     * @param packageName packagename of the app which uses the partner library
     * @return  returns newly created datastring
     */
    private static String createMetaDataString(Context context, String packageName) {
        StringBuilder dataString = new StringBuilder();
        // get permission list

        //get certificate
        return dataString.toString();
    }

    /**
     * This method creates the data string to be verified.
     *
     * @param packageInfo packageinfo of the verifier library service
     * @param packageName packagename of the app which uses the partner library
     * @return newly created datastring
     */
    private static String createMetaDataString(PackageInfo packageInfo, String packageName) {
        // Form the meta-data string to be verified
        StringBuilder dataString = new StringBuilder(packageName).append(";");
        dataString.append(
                String.join(";", getFilteredCariadPermissionList(packageInfo.requestedPermissions)));
        dataString.append(";" + getSigningCertificatesString(packageInfo.signingInfo));
        return dataString.toString();
    }

    /**
     * This method filters the list of permissions and returns a sorted list of permissions starting
     * with {@link Utils#CARIAD_VWAE_RESTRICTED_NAMESPACE}
     *
     * @param permissionList list of permission strings to be filtered.
     * @return sorted list of permissions starting with {@link Utils#CARIAD_VWAE_RESTRICTED_NAMESPACE}
     */
    private static List<String> getFilteredCariadPermissionList(String[] permissionList) {
        List<String> filteredPermissionList = new ArrayList<>();

        for (String permission : permissionList) {
            Log.d(TAG, "permission Name: " + permission);
            if (permission.startsWith(CARIAD_VWAE_RESTRICTED_NAMESPACE)) {
                filteredPermissionList.add(permission);
            }
        }
        Collections.sort(filteredPermissionList);

        return filteredPermissionList;
    }

    /**
     * This method creates a string representation of Signing Certificates for verification.
     *
     * @param signingInfo signing info of partner application's signature certificates.
     * @return newly created string representation of signing certificates.
     */
    private static StringBuilder getSigningCertificatesString(SigningInfo signingInfo) {
        if (signingInfo == null) return null;
        StringBuilder sb = new StringBuilder();

        android.content.pm.Signature[] sigs = signingInfo.getApkContentsSigners();
        for (android.content.pm.Signature sig : sigs) {
            final byte[] rawCert = sig.toByteArray();
            InputStream certStream = new ByteArrayInputStream(rawCert);
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
                X509Certificate x509Certificate =
                        (X509Certificate) certificateFactory.generateCertificate(certStream);
                RSAPublicKey rsaPublicKey = (RSAPublicKey) x509Certificate.getPublicKey();
                sb.append(
                        x509Certificate.getVersion()
                                + x509Certificate.getSerialNumber().toString()
                                + x509Certificate.getSubjectDN().toString()
                                + x509Certificate.getIssuerDN().toString()
                                + x509Certificate.getIssuerUniqueID()
                                + x509Certificate.getSigAlgName()
                                + x509Certificate.getSigAlgOID());

                sb.append(
                        rsaPublicKey.getModulus().toString(16) + rsaPublicKey.getPublicExponent().toString(16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return sb;
    }

    /**
     * This method uses the Signature engine to verify the app digital signature.
     *
     * @param pubKey  publickey from the certificate whose private key is used to sign
     * @param metadata metadata of the app from its manifest file/packagemanager
     * @param dataString string to be verified
     * @return  returns true if verification succeeds. false if verification fails.
     */
    private static boolean verifySignature(PublicKey pubKey, String metadata, String dataString) {
        boolean res = false;
        try {
            byte[] dataBytes = dataString.getBytes(); // TODO: encoding!
            Base64.Decoder mimeDecoder = Base64.getMimeDecoder();
            Signature sig = Signature.getInstance("SHA256WithRSA");
            sig.initVerify(pubKey);
            byte[] metadataByte = mimeDecoder.decode(metadata);
            Log.d(TAG,"Metadata value Bytes: " + metadataByte);
            sig.update(dataBytes);
            res = sig.verify(metadataByte);
            Log.d(TAG,"Verification result: " + res);
        } catch (InvalidKeyException | SignatureException |
                IllegalArgumentException | NoSuchAlgorithmException e) {
            Log.d(TAG,"Exception message " + e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get the public key from the certificate .
     *
     * @param context context of the verifier library service
     * @return returns the PublicKey if available or null.
     */
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
