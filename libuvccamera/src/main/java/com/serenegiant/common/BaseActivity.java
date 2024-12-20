package com.serenegiant.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.serenegiant.dialog.MessageDialogFragmentV4;
import com.serenegiant.utils.HandlerThreadHandler;
import com.serenegiant.utils.PermissionCheck;

public class BaseActivity extends AppCompatActivity
        implements MessageDialogFragmentV4.MessageDialogListener {

    private static final boolean DEBUG = false; // Set to false for production
    private static final String TAG = BaseActivity.class.getSimpleName();

    private final Handler mUIHandler = new Handler(Looper.getMainLooper());
    private final Thread mUiThread = mUIHandler.getLooper().getThread();
    private Handler mWorkerHandler;
    private long mWorkerThreadID = -1;
    private Toast mToast;
    private ShowToastTask mShowToastTask;

    // Permission request codes
    protected static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x12345;
    protected static final int REQUEST_PERMISSION_AUDIO_RECORDING = 0x234567;
    protected static final int REQUEST_PERMISSION_NETWORK = 0x345678;
    protected static final int REQUEST_PERMISSION_CAMERA = 0x537642;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mWorkerHandler == null) {
            mWorkerHandler = HandlerThreadHandler.createHandler(TAG);
            mWorkerThreadID = mWorkerHandler.getLooper().getThread().getId();
        }
    }

    @Override
    protected void onPause() {
        clearToast();
        super.onPause();
    }

    @Override
    protected synchronized void onDestroy() {
        if (mWorkerHandler != null) {
            try {
                mWorkerHandler.getLooper().quit();
            } catch (Exception e) {
                Log.w(TAG, "Error while quitting worker thread", e);
            }
            mWorkerHandler = null;
        }
        super.onDestroy();
    }

    public final void runOnUiThread(final Runnable task, final long delayMillis) {
        if (task == null) return;
        mUIHandler.removeCallbacks(task);
        if (delayMillis > 0 || Thread.currentThread() != mUiThread) {
            mUIHandler.postDelayed(task, delayMillis);
        } else {
            task.run();
        }
    }

    public final void removeFromUiThread(final Runnable task) {
        if (task != null) {
            mUIHandler.removeCallbacks(task);
        }
    }

    protected final synchronized void queueEvent(final Runnable task, final long delayMillis) {
        if (task == null || mWorkerHandler == null) return;
        mWorkerHandler.removeCallbacks(task);
        if (delayMillis > 0) {
            mWorkerHandler.postDelayed(task, delayMillis);
        } else if (mWorkerThreadID == Thread.currentThread().getId()) {
            task.run();
        } else {
            mWorkerHandler.post(task);
        }
    }

    protected final synchronized void removeEvent(final Runnable task) {
        if (task != null && mWorkerHandler != null) {
            mWorkerHandler.removeCallbacks(task);
        }
    }

    protected void showToast(@StringRes int msg, Object... args) {
        removeFromUiThread(mShowToastTask);
        mShowToastTask = new ShowToastTask(msg, args);
        runOnUiThread(mShowToastTask, 0);
    }

    protected void clearToast() {
        removeFromUiThread(mShowToastTask);
        mShowToastTask = null;
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    @Override
    public void onMessageDialogResult(MessageDialogFragmentV4 dialog, int requestCode, String[] permissions, boolean result) {
        if (result) {
            requestPermissions(permissions, requestCode);
        } else {
            for (String permission : permissions) {
                checkPermissionResult(requestCode, permission, PermissionCheck.hasPermission(this, permission));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            checkPermissionResult(requestCode, permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
    }

    protected void checkPermissionResult(int requestCode, String permission, boolean granted) {
        if (!granted && permission != null) {
            switch (permission) {
                case Manifest.permission.RECORD_AUDIO:
                    showToast(R.string.permission_audio);
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    showToast(R.string.permission_ext_storage);
                    break;
                case Manifest.permission.INTERNET:
                    showToast(R.string.permission_network);
                    break;
                case Manifest.permission.CAMERA:
                    showToast(R.string.permission_camera);
                    break;
            }
        }
    }

    protected boolean checkPermissionWriteExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return true;
        if (!PermissionCheck.hasWriteExternalStorage(this)) {
            MessageDialogFragmentV4.showDialog(this, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                    R.string.permission_title, R.string.permission_ext_storage_request,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            return false;
        }
        return true;
    }

    protected boolean checkPermissionAudio() {
        if (!PermissionCheck.hasAudio(this)) {
            MessageDialogFragmentV4.showDialog(this, REQUEST_PERMISSION_AUDIO_RECORDING,
                    R.string.permission_title, R.string.permission_audio_recording_request,
                    new String[]{Manifest.permission.RECORD_AUDIO});
            return false;
        }
        return true;
    }

    protected boolean checkPermissionNetwork() {
        if (!PermissionCheck.hasNetwork(this)) {
            MessageDialogFragmentV4.showDialog(this, REQUEST_PERMISSION_NETWORK,
                    R.string.permission_title, R.string.permission_network_request,
                    new String[]{Manifest.permission.INTERNET});
            return false;
        }
        return true;
    }

    protected boolean checkPermissionCamera() {
        if (!PermissionCheck.hasCamera(this)) {
            MessageDialogFragmentV4.showDialog(this, REQUEST_PERMISSION_CAMERA,
                    R.string.permission_title, R.string.permission_camera_request,
                    new String[]{Manifest.permission.CAMERA});
            return false;
        }
        return true;
    }

    private class ShowToastTask implements Runnable {
        @StringRes
        private final int msg;
        private final Object[] args;

        ShowToastTask(@StringRes int msg, Object... args) {
            this.msg = msg;
            this.args = args;
        }

        @Override
        public void run() {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(BaseActivity.this,
                    (args != null) ? getString(msg, args) : getString(msg),
                    Toast.LENGTH_SHORT);
            mToast.show();
        }
    }
}
