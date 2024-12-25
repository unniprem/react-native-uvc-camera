package com.serenegiant.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.serenegiant.dialog.MessageDialogFragmentV4;
import com.serenegiant.utils.HandlerThreadHandler;
import com.serenegiant.utils.PermissionCheck;

public class BaseActivity extends FragmentActivity implements MessageDialogFragmentV4.MessageDialogListener {
    private static boolean DEBUG = false;
    private static final String TAG = BaseActivity.class.getSimpleName();

    private final Handler mUIHandler = new Handler(Looper.getMainLooper());
    private final Thread mUiThread = mUIHandler.getLooper().getThread();
    private Handler mWorkerHandler;
    private long mWorkerThreadID = -1;

    // Request codes for permissions
    protected static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x12345;
    protected static final int REQUEST_PERMISSION_AUDIO_RECORDING = 0x234567;
    protected static final int REQUEST_PERMISSION_NETWORK = 0x345678;
    protected static final int REQUEST_PERMISSION_CAMERA = 0x537642;

    private Toast mToast;
    private ShowToastTask mShowToastTask;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
            } catch (final Exception e) {
                // ignore
            }
            mWorkerHandler = null;
        }
        super.onDestroy();
    }

    public final void runOnUiThread(final Runnable task, final long duration) {
        if (task == null) return;
        mUIHandler.removeCallbacks(task);
        if ((duration > 0) || Thread.currentThread() != mUiThread) {
            mUIHandler.postDelayed(task, duration);
        } else {
            try {
                task.run();
            } catch (final Exception e) {
                Log.w(TAG, e);
            }
        }
    }

    public final void removeFromUiThread(final Runnable task) {
        if (task == null) return;
        mUIHandler.removeCallbacks(task);
    }

    protected final synchronized void queueEvent(final Runnable task, final long delayMillis) {
        if ((task == null) || (mWorkerHandler == null)) return;
        try {
            mWorkerHandler.removeCallbacks(task);
            if (delayMillis > 0) {
                mWorkerHandler.postDelayed(task, delayMillis);
            } else if (mWorkerThreadID == Thread.currentThread().getId()) {
                task.run();
            } else {
                mWorkerHandler.post(task);
            }
        } catch (final Exception e) {
            // ignore
        }
    }

    protected final synchronized void removeEvent(final Runnable task) {
        if (task == null) return;
        try {
            mWorkerHandler.removeCallbacks(task);
        } catch (final Exception e) {
            // ignore
        }
    }

    protected void showToast(@StringRes final int msg, final Object... args) {
        removeFromUiThread(mShowToastTask);
        mShowToastTask = new ShowToastTask(msg, args);
        runOnUiThread(mShowToastTask, 0);
    }

    protected void clearToast() {
        removeFromUiThread(mShowToastTask);
        mShowToastTask = null;
        try {
            if (mToast != null) {
                mToast.cancel();
                mToast = null;
            }
        } catch (final Exception e) {
            // ignore
        }
    }

    private class ShowToastTask implements Runnable {
        final int msg;
        final Object[] args;

        private ShowToastTask(final int msg, final Object... args) {
            this.msg = msg;
            this.args = args;
        }

        @Override
        public void run() {
            try {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                final String _msg = (args != null) ? BaseActivity.this.getString(msg, args) : BaseActivity.this.getString(msg);
                mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), _msg, Toast.LENGTH_SHORT);
                mToast.show();
            } catch (final Exception e) {
                // ignore
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onMessageDialogResult(final MessageDialogFragmentV4 dialog, final int requestCode, 
            final String[] permissions, final boolean result) {
        if (result) {
            requestPermissions(permissions, requestCode);
            return;
        }
        for (final String permission: permissions) {
            checkPermissionResult(requestCode, permission, PermissionCheck.hasPermission(getApplicationContext(), permission));
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final int n = Math.min(permissions.length, grantResults.length);
        for (int i = 0; i < n; i++) {
            checkPermissionResult(requestCode, permissions[i], 
                grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
    }

    protected void checkPermissionResult(final int requestCode, final String permission, 
            final boolean result) {
        if (!result && (permission != null)) {
            if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
                showToast(R.string.permission_audio);
            }
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                showToast(R.string.permission_ext_storage);
            }
            if (Manifest.permission.INTERNET.equals(permission)) {
                showToast(R.string.permission_network);
            }
        }
    }

    protected boolean checkPermissionWriteExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return true;
        if (!PermissionCheck.hasWriteExternalStorage(getApplicationContext())) {
            MessageDialogFragmentV4.showDialog(this, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                R.string.permission_title, R.string.permission_ext_storage_request,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            return false;
        }
        return true;
    }

    protected boolean checkPermissionAudio() {
        if (!PermissionCheck.hasAudio(getApplicationContext())) {
            MessageDialogFragmentV4.showDialog(this, REQUEST_PERMISSION_AUDIO_RECORDING,
                R.string.permission_title, R.string.permission_audio_recording_request,
                new String[]{Manifest.permission.RECORD_AUDIO});
            return false;
        }
        return true;
    }

    protected boolean checkPermissionNetwork() {
        if (!PermissionCheck.hasNetwork(getApplicationContext())) {
            MessageDialogFragmentV4.showDialog(this, REQUEST_PERMISSION_NETWORK,
                R.string.permission_title, R.string.permission_network_request,
                new String[]{Manifest.permission.INTERNET});
            return false;
        }
        return true;
    }

    protected boolean checkPermissionCamera() {
        if (!PermissionCheck.hasCamera(getApplicationContext())) {
            MessageDialogFragmentV4.showDialog(this, REQUEST_PERMISSION_CAMERA,
                R.string.permission_title, R.string.permission_camera_request,
                new String[]{Manifest.permission.CAMERA});
            return false;
        }
        return true;
    }
}