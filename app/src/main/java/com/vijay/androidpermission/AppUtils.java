package com.vijay.androidpermission;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AppUtils {

    static void messageLayout(@NonNull Context mContext, @NonNull View view, @NonNull String mMsg, @NonNull String mBtnText, final MessageListener mListner) {
        TextView tvMsg = view.findViewById(R.id.tvMessage);
        if (tvMsg != null)
            tvMsg.setText(mMsg.trim().isEmpty() ? mContext.getString(R.string.msg_no_internet) : mMsg.trim());
        Button btnAllow = view.findViewById(R.id.btnRetry);
        if (btnAllow != null) {
            btnAllow.setText(mBtnText.trim().isEmpty() ? mContext.getString(R.string.label_retry) : mBtnText.trim());
            btnAllow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListner != null)
                        mListner.onMessageClicked();
                }
            });
        }
    }

    static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public interface MessageListener {
        void onMessageClicked();
    }
}