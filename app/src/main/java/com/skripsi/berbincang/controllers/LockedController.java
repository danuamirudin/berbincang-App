

package com.skripsi.berbincang.controllers;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;
import autodagger.AutoInjector;
import butterknife.OnClick;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.controllers.base.BaseController;
import com.skripsi.berbincang.utils.SecurityUtils;
import com.skripsi.berbincang.utils.preferences.AppPreferences;

import javax.inject.Inject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@AutoInjector(NextcloudTalkApplication.class)
public class LockedController extends BaseController {
    public static final String TAG = "LockedController";
    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 112;

    @Inject
    AppPreferences appPreferences;

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.controller_locked, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);
        if (getActionBar() != null) {
            getActionBar().hide();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        checkIfWeAreSecure();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.unlockTextView)
    void unlock() {
        checkIfWeAreSecure();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showBiometricDialog() {
        Context context = getActivity();

        if (context != null) {
            final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(String.format(context.getString(R.string.nc_biometric_unlock), context.getString(R.string.nc_app_name)))
                    .setNegativeButtonText(context.getString(R.string.nc_cancel))
                    .build();

            Executor executor = Executors.newSingleThreadExecutor();

            final BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) context, executor,
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            Log.d(TAG, "Fingerprint recognised successfully");
                            new Handler(Looper.getMainLooper()).post(() -> getRouter().popCurrentController());
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            Log.d(TAG, "Fingerprint not recognised");
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            showAuthenticationScreen();
                        }
                    }
            );

            BiometricPrompt.CryptoObject cryptoObject = SecurityUtils.getCryptoObject();
            if (cryptoObject != null) {
                biometricPrompt.authenticate(promptInfo, cryptoObject);
            } else {
                biometricPrompt.authenticate(promptInfo);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkIfWeAreSecure() {
        if (getActivity() != null) {
            KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null && keyguardManager.isKeyguardSecure() && appPreferences.getIsScreenLocked()) {
                if (!SecurityUtils.checkIfWeAreAuthenticated(appPreferences.getScreenLockTimeout())) {
                    showBiometricDialog();
                } else {
                    getRouter().popCurrentController();
                }
            }
        }
    }

    private void showAuthenticationScreen() {
        if (getActivity() != null) {
            KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);
            Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (SecurityUtils.checkIfWeAreAuthenticated(appPreferences.getScreenLockTimeout())) {
                        Log.d(TAG, "All went well, dismiss locked controller");
                        getRouter().popCurrentController();
                    }
                }
            } else {
                Log.d(TAG, "Authorization failed");
            }
        }
    }
}
