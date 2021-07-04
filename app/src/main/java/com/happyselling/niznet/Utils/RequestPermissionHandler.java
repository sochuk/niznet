package com.happyselling.niznet.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class RequestPermissionHandler {
    private Context mContext;
    private RequestPermissionListener mRequestPermissionListener;
    private int mRequestCode;

    public void requestPermission(Context mContext, @NonNull String[] permissions, int requestCode,
                                  RequestPermissionListener listener) {
        mContext = mContext;
        mRequestCode = requestCode;
        mRequestPermissionListener = listener;

        if (!needRequestRuntimePermissions()) {
            mRequestPermissionListener.onSuccess();
            return;
        }
        requestUnGrantedPermissions(permissions, requestCode);
    }

    private boolean needRequestRuntimePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void requestUnGrantedPermissions(String[] permissions, int requestCode) {
        String[] unGrantedPermissions = findUnGrantedPermissions(permissions);
        if (unGrantedPermissions.length == 0) {
            mRequestPermissionListener.onSuccess();
            return;
        }
        ActivityCompat.requestPermissions((Activity)mContext, unGrantedPermissions, requestCode);
    }

    private boolean isPermissionGranted(String permission) {
        return ActivityCompat.checkSelfPermission((Activity)mContext, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private String[] findUnGrantedPermissions(String[] permissions) {
        List<String> unGrantedPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (!isPermissionGranted(permission)) {
                unGrantedPermissionList.add(permission);
            }
        }
        return unGrantedPermissionList.toArray(new String[0]);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == mRequestCode) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mRequestPermissionListener.onFailed();
                        return;
                    }
                }
                mRequestPermissionListener.onSuccess();
            } else {
                mRequestPermissionListener.onFailed();
            }
        }
    }

    public interface RequestPermissionListener {
        void onSuccess();

        void onFailed();
    }
}
