package com.vijay.androidpermission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import static com.vijay.androidpermission.AppConstant.OVERLAY_PERMISSION_CODE;
import static com.vijay.androidpermission.AppConstant.PERMISSION_CODE;

class PermissionHandler {
    private static PermissionHandler mInstance = null;
    private Context mContext;
    private boolean isPermissionDenied = false;
    private PermissionCallback permissionCallback;
    private AlertDialog mAlertDialog;

    /**
     * here you can add required permission
     */
    private ArrayList<String> permissionList = new ArrayList<String>() {{
        add(Manifest.permission.ACCESS_COARSE_LOCATION);
        add(Manifest.permission.ACCESS_FINE_LOCATION);
        add(Manifest.permission.CAMERA);
        add(Manifest.permission.READ_EXTERNAL_STORAGE);
        add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        add(Manifest.permission.READ_SMS);
    }};

    public static PermissionHandler getInstance() {
        if (mInstance == null) {
            synchronized (PermissionHandler.class) {
                mInstance = new PermissionHandler();
            }
        }
        return mInstance;
    }

    public void checkForPermission(String... permission) {
        if (permission.length > 0) {
            permissionList.clear();
            permissionList.addAll(Arrays.asList(permission));
        }
        if (permissionCallback == null)
            return;

        if (!hasAllPermission())
            permissionCallback.onPermissionNeeded();
        else
            permissionCallback.onPermissionValid();
    }

    public boolean hasAllPermission() {
        boolean isAllowed = true;
        for (String permission : permissionList) {
            isAllowed = ActivityCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
            if (!isAllowed)
                break;
        }
        return isAllowed;
    }

    public void requestPermission(String... permission) {
        if (permission.length > 0) {
            permissionList.clear();
            permissionList.addAll(Arrays.asList(permission));
        }

        if (mContext instanceof Activity)
            ActivityCompat.requestPermissions((Activity) mContext, permissionList.toArray(new String[0]), PERMISSION_CODE);
    }

    public void onPermissionRequestResult(int requestCode, String[] permission) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (!hasAllPermission()) {
                    if (mContext instanceof Activity && permission.length > 0)
                        isPermissionDenied = ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permission[1]);
                    if (!isPermissionDenied) {
                        if (permissionCallback != null)
                            permissionCallback.onPermissionDenied();
                        showDenyPopUp();
                    }
                } else if (permissionList != null)
                    permissionCallback.onPermissionValid();
                break;

            case OVERLAY_PERMISSION_CODE:
                Toast.makeText(mContext, "overlay permission", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showDenyPopUp() {
        if (mAlertDialog != null && mAlertDialog.isShowing())
            return;
        mAlertDialog = getDialog();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                Button alertPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                alertPositiveButton.setTextSize(16);
                alertPositiveButton.setAllCaps(false);
                alertPositiveButton.setTextColor(mContext.getResources().getColor(android.R.color.white));
                alertPositiveButton.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_green_light));
            }
        });

        mAlertDialog.show();
    }

    private AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.alert_message, null);
        builder.setView(view);
        builder.setPositiveButton(mContext.getString(R.string.settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!isPermissionDenied)
                    openPhoneSetting();
                else
                    requestPermission(permissionList.toArray(new String[0]));
            }
        });
        builder.setCancelable(false);
        return builder.create();
    }

    private void openPhoneSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", mContext.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public PermissionHandler getContext(Context context) {
        this.mContext = context;
        return this;
    }

    public PermissionHandler addPermissionListener(PermissionCallback permissionCallback) {
        this.permissionCallback = permissionCallback;
        return this;
    }
}
