

package com.skripsi.berbincang.jobs;


import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import autodagger.AutoInjector;
import com.bluelinelabs.logansquare.LoganSquare;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.api.NcApi;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.models.json.generic.GenericOverall;
import com.skripsi.berbincang.models.json.push.PushConfigurationState;
import com.skripsi.berbincang.utils.ApiUtils;
import com.skripsi.berbincang.utils.database.arbitrarystorage.ArbitraryStorageUtils;
import com.skripsi.berbincang.utils.database.user.UserUtils;
import com.skripsi.berbincang.webrtc.WebSocketConnectionHelper;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.io.IOException;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.zip.CRC32;

@AutoInjector(NextcloudTalkApplication.class)
public class AccountRemovalWorker extends Worker {
    public static final String TAG = "AccountRemovalWorker";

    @Inject
    UserUtils userUtils;

    @Inject
    ArbitraryStorageUtils arbitraryStorageUtils;

    @Inject
    Retrofit retrofit;

    @Inject
    OkHttpClient okHttpClient;

    NcApi ncApi;

    public AccountRemovalWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);

        PushConfigurationState pushConfigurationState;
        String credentials;
        for (Object userEntityObject : userUtils.getUsersScheduledForDeletion()) {
            UserEntity userEntity = (UserEntity) userEntityObject;
            try {
                if (!TextUtils.isEmpty(userEntity.getPushConfigurationState())) {
                    pushConfigurationState = LoganSquare.parse(userEntity.getPushConfigurationState(),
                            PushConfigurationState.class);
                    PushConfigurationState finalPushConfigurationState = pushConfigurationState;

                    credentials = ApiUtils.getCredentials(userEntity.getUsername(), userEntity.getToken());

                    ncApi = retrofit.newBuilder().client(okHttpClient.newBuilder().cookieJar(new
                            JavaNetCookieJar(new CookieManager())).build()).build().create(NcApi.class);

                    String finalCredentials = credentials;
                    ncApi.unregisterDeviceForNotificationsWithNextcloud(credentials, ApiUtils.getUrlNextcloudPush(userEntity
                            .getBaseUrl()))
                            .blockingSubscribe(new Observer<GenericOverall>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(GenericOverall genericOverall) {
                                    if (genericOverall.getOcs().getMeta().getStatusCode() == 200
                                            || genericOverall.getOcs().getMeta().getStatusCode() == 202) {
                                        HashMap<String, String> queryMap = new HashMap<>();
                                        queryMap.put("deviceIdentifier", finalPushConfigurationState.deviceIdentifier);
                                        queryMap.put("userPublicKey", finalPushConfigurationState.getUserPublicKey());
                                        queryMap.put("deviceIdentifierSignature",
                                                finalPushConfigurationState.getDeviceIdentifierSignature());

                                        ncApi.unregisterDeviceForNotificationsWithProxy
                                                (ApiUtils.getUrlPushProxy(), queryMap)
                                                .subscribe(new Observer<Void>() {
                                                    @Override
                                                    public void onSubscribe(Disposable d) {

                                                    }

                                                    @Override
                                                    public void onNext(Void aVoid) {

                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                            String groupName = String.format(getApplicationContext().getResources()
                                                                    .getString(R.string
                                                                            .nc_notification_channel), userEntity.getUserId(), userEntity.getBaseUrl());
                                                            CRC32 crc32 = new CRC32();
                                                            crc32.update(groupName.getBytes());
                                                            NotificationManager notificationManager =
                                                                    (NotificationManager) getApplicationContext().getSystemService
                                                                            (Context.NOTIFICATION_SERVICE);

                                                            if (notificationManager != null) {
                                                                notificationManager.deleteNotificationChannelGroup(Long
                                                                        .toString(crc32.getValue()));
                                                            }
                                                        }

                                                        WebSocketConnectionHelper.deleteExternalSignalingInstanceForUserEntity(userEntity.getId());

                                                        arbitraryStorageUtils.deleteAllEntriesForAccountIdentifier(userEntity.getId()).subscribe(new Observer() {
                                                            @Override
                                                            public void onSubscribe(Disposable d) {

                                                            }

                                                            @Override
                                                            public void onNext(Object o) {
                                                                userUtils.deleteUser(userEntity.getId()).subscribe(new CompletableObserver() {
                                                                    @Override
                                                                    public void onSubscribe(Disposable d) {

                                                                    }

                                                                    @Override
                                                                    public void onComplete() {

                                                                    }

                                                                    @Override
                                                                    public void onError(Throwable e) {

                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {

                                                            }

                                                            @Override
                                                            public void onComplete() {

                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {

                                                    }

                                                    @Override
                                                    public void onComplete() {

                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                } else {
                    userUtils.deleteUser(userEntity.getId())
                            .subscribe(new CompletableObserver() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onComplete() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }
                            });
                }
            } catch (IOException e) {
                Log.d(TAG, "Something went wrong while removing job at parsing PushConfigurationState");
                userUtils.deleteUser(userEntity.getId())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
            }
        }

        return Result.success();
    }
}
