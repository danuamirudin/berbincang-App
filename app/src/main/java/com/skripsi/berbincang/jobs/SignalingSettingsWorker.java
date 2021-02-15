

package com.skripsi.berbincang.jobs;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.*;
import autodagger.AutoInjector;
import com.bluelinelabs.logansquare.LoganSquare;
import com.skripsi.berbincang.api.NcApi;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.events.EventStatus;
import com.skripsi.berbincang.models.ExternalSignalingServer;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.models.json.signaling.settings.SignalingSettingsOverall;
import com.skripsi.berbincang.utils.ApiUtils;
import com.skripsi.berbincang.utils.bundle.BundleKeys;
import com.skripsi.berbincang.utils.database.user.UserUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AutoInjector(NextcloudTalkApplication.class)
public class SignalingSettingsWorker extends Worker {
    private static final String TAG = "SignalingSettingsJob";

    @Inject
    UserUtils userUtils;

    @Inject
    NcApi ncApi;

    @Inject
    EventBus eventBus;

    public SignalingSettingsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);

        Data data = getInputData();

        long internalUserId = data.getLong(BundleKeys.INSTANCE.getKEY_INTERNAL_USER_ID(), -1);

        List<UserEntity> userEntityList = new ArrayList<>();
        UserEntity userEntity;
        if (internalUserId == -1 || (userEntity = userUtils.getUserWithInternalId(internalUserId)) == null) {
            userEntityList = userUtils.getUsers();
        } else {
            userEntityList.add(userEntity);
        }

        for (int i = 0; i < userEntityList.size(); i++) {
            userEntity = userEntityList.get(i);
            UserEntity finalUserEntity = userEntity;
            ncApi.getSignalingSettings(ApiUtils.getCredentials(userEntity.getUsername(), userEntity.getToken()),
                    ApiUtils.getUrlForSignalingSettings(userEntity.getBaseUrl()))
                    .blockingSubscribe(new Observer<SignalingSettingsOverall>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(SignalingSettingsOverall signalingSettingsOverall) {
                            ExternalSignalingServer externalSignalingServer;
                            externalSignalingServer = new ExternalSignalingServer();
                            externalSignalingServer.setExternalSignalingServer(signalingSettingsOverall.getOcs().getSettings().getExternalSignalingServer());
                            externalSignalingServer.setExternalSignalingTicket(signalingSettingsOverall.getOcs().getSettings().getExternalSignalingTicket());

                            try {
                                userUtils.createOrUpdateUser(null, null, null, null, null,
                                        null, null, finalUserEntity.getId(), null, null, LoganSquare.serialize(externalSignalingServer))
                                        .subscribe(new Observer<UserEntity>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onNext(UserEntity userEntity) {
                                                eventBus.post(new EventStatus(finalUserEntity.getId(), EventStatus.EventType.SIGNALING_SETTINGS, true));
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                eventBus.post(new EventStatus(finalUserEntity.getId(), EventStatus.EventType.SIGNALING_SETTINGS, false));
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            } catch (IOException e) {
                                Log.e(TAG, "Failed to serialize external signaling server");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            eventBus.post(new EventStatus(finalUserEntity.getId(), EventStatus.EventType.SIGNALING_SETTINGS, false));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        OneTimeWorkRequest websocketConnectionsWorker = new OneTimeWorkRequest.Builder(WebsocketConnectionsWorker.class).build();
        WorkManager.getInstance().enqueue(websocketConnectionsWorker);

        return Result.success();
    }
}
