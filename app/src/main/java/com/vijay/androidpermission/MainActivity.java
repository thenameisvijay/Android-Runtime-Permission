package com.vijay.androidpermission;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements PermissionCallback, AppUtils.MessageListener {

    private View llMessageLayout;
    private View llContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        llMessageLayout = findViewById(R.id.llMessageLayout);
        llContainer = findViewById(R.id.llContainer);
        llContainer.setVisibility(View.GONE);
        AppUtils.messageLayout(this, llMessageLayout, getString(R.string.permission_msg), getString(R.string.label_allow), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PermissionHandler.getInstance().getContext(this).addPermissionListener(this).checkForPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHandler.getInstance().getContext(this).onPermissionRequestResult(requestCode, permissions);
    }

    @Override
    public void onPermissionNeeded() {
        llContainer.setVisibility(View.GONE);
        llMessageLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPermissionDenied() {

    }

    @Override
    public void onPermissionValid() {
        llMessageLayout.setVisibility(View.GONE);
        llContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMessageClicked() {
        PermissionHandler.getInstance().getContext(this).requestPermission();
    }
}
