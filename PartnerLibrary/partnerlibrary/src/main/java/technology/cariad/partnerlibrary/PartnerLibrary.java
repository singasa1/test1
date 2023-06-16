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
package technology.cariad.partnerlibrary;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import technology.cariad.partnerenablerservice.IPartnerEnabler;
import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Partner Library</h1>
 * Partner Library provides wrapper apis for different app developers.
 * It has signature verification apis and other apis for getting the Active Route, Interior/Exterior Light status.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-04-20
 */
public class PartnerLibrary {
    private static final String TAG = PartnerLibrary.class.getSimpleName();

    private IPartnerEnabler mService;
    private PartnerEnablerServiceConnection mServiceConnection;
    private Context mContext;
    private boolean mIsPartnerEnablerServiceConnected = false;
    private List<ILibStateChangeListener> mClientListeners = new ArrayList<>();

    private static final String PARTNER_ENABLER_SERVICE_NAME = "technology.cariad.partnerenablerservice.enabler";
    private static final String PARTNER_ENABLER_SERVICE_PACKAGE_NAME = "technology.cariad.partnerenablerservice";

    /**
     * This class represents the actual service connection. It casts the bound
     * stub implementation of the service to the AIDL interface.
     */
    class PartnerEnablerServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            mService = IPartnerEnabler.Stub.asInterface((IBinder) boundService);
            Log.d(TAG, "onServiceConnected() connected");
            mIsPartnerEnablerServiceConnected = true;
            if (mClientListeners != null) {
                try {
                    Log.d(TAG, "calling listener onLibStateReady with value: " + mIsPartnerEnablerServiceConnected);
                    for(ILibStateChangeListener listener: mClientListeners) {
                        listener.onStateChanged(mIsPartnerEnablerServiceConnected);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.d(TAG, "onServiceDisconnected() disconnected");
            mIsPartnerEnablerServiceConnected = false;
            if (mClientListeners != null) {
                try {
                    Log.d(TAG, "calling listener onLibStateReady with value: " + mIsPartnerEnablerServiceConnected);
                    for(ILibStateChangeListener listener: mClientListeners) {
                        listener.onStateChanged(mIsPartnerEnablerServiceConnected);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PartnerLibrary(Context context) {
        Log.d(TAG,"PartnerLibrary");
        mContext = context;
    }

    /**
     * This method checks whether the PartnerEnablerService is installed or not.
     * If installed, check the version number of PartnerenablerService
     * @return true - if correct version of PartnerEnablerService is installed.
     */
    public boolean isPartnerEnablerServiceReady() {
        try {
            mContext.getPackageManager().getApplicationInfo(PARTNER_ENABLER_SERVICE_PACKAGE_NAME, 0);
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(PARTNER_ENABLER_SERVICE_PACKAGE_NAME, 0);
            Log.i(TAG,"PackageVersionName: " + packageInfo.versionName + ",versionCode; " + packageInfo.getLongVersionCode());
            long versionNumber = packageInfo.getLongVersionCode();
            if (versionNumber <= 1) return false;
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * This method creates AlertDialog with 3rd PartyAppName as Tile and description and
     * positive button with url to AppStore to download+install PES. Negative button to
     * skip the installation.
     */
    public void requestUserToInstallDependency() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);

        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        //builder.setMessage("Do you want to close this application ?")
        builder.setCancelable(false)
                .setPositiveButton("Install from AppStore", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Toast.makeText(mContext,"you choose Install from AppStore action for alertbox",
                                Toast.LENGTH_SHORT).show();
                        showContacts();
                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(mContext,"you choose Skip action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        ApplicationInfo applicationInfo = mContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        String appName = stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : mContext.getString(stringId);
        alert.setTitle(appName);
        alert.show();
    }

    private void showContacts()
    {
        Intent i = new Intent();
        i.setComponent(new ComponentName("com.android.contacts", "com.android.contacts.DialtactsContactsEntryActivity"));
        i.setAction("android.intent.action.MAIN");
        i.addCategory("android.intent.category.LAUNCHER");
        i.addCategory("android.intent.category.DEFAULT");
        mContext.startActivity(i);
    }

    /**
     * This method binds to the PartnerEnabler service.
     */
    public void initialize() {
        Log.d(TAG,"initialize required services");
        // bind to the enabler service.
        initService();
    }

    /**
     * This method unbinds the PartnerEnabler service
     */
    public void release() {
        Log.d(TAG,"release");
        // unbind service
        releaseService();

    }

    /**
     * This method initializes the PartnerEnabler service components
     */
    public void start() {
        Log.d(TAG,"start");
        if (mIsPartnerEnablerServiceConnected) {
            try {
                mService.initialize();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method uninitializes the PartnerEnabler service components
     */
    public void stop() {
        Log.d(TAG,"stop");
        if (mIsPartnerEnablerServiceConnected) {
            try {
                mService.release();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is to add the listener to get PartnerEnablerServiceConnection status.
     * @param listener ILibStateChangeListener object from client/app.
     */
    public void addListener(ILibStateChangeListener listener) {
        mClientListeners.add(listener);
    }

    /**
     * This method is to remove the listener.
     */
    public void removeListener(ILibStateChangeListener listener) {
        mClientListeners.remove(listener);
    }

    /**
     * This method verifies the provided package signature
     * matches with signed config provided by the SignatureGenerator tool.
     * @param packageName Package name of the 3rd party app.
     * @return true - if signature verification succeeds. False - if signature verification fails.
     */
    public boolean verifyDigitalSignature(@NonNull String packageName) {
        boolean retVal = false;
        if (mIsPartnerEnablerServiceConnected) {
            try {
                ISignatureVerifier verifier = mService.getPartnerVerifierService();
                retVal = verifier.verifyDigitalSignature(packageName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    /** Binds the user activity to the service. */
    private void initService() {
        Log.d(TAG,"initService trying to bindService");
        mServiceConnection = new PartnerEnablerServiceConnection();
        Intent i = new Intent(PARTNER_ENABLER_SERVICE_NAME).setPackage(PARTNER_ENABLER_SERVICE_PACKAGE_NAME);
        boolean ret = mContext.bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "initService() bound with " + ret);
    }

    /** Unbinds the user activity from the service. */
    private void releaseService() {
        mContext.unbindService(mServiceConnection);
        mServiceConnection = null;
        Log.d(TAG, "releaseService() unbound.");
    }
}