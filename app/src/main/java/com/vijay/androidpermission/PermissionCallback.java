package com.vijay.androidpermission;

public interface PermissionCallback {

    void onPermissionNeeded();

    void onPermissionDenied();

    void onPermissionValid();
}
