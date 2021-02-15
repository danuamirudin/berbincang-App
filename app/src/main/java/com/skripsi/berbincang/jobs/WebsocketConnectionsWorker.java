

package com.skripsi.berbincang.jobs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import autodagger.AutoInjector;
import com.bluelinelabs.logansquare.LoganSquare;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.models.ExternalSignalingServer;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.utils.database.user.UserUtils;
import com.skripsi.berbincang.webrtc.WebSocketConnectionHelper;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@AutoInjector(NextcloudTalkApplication.class)
public class WebsocketConnectionsWorker extends Worker {

    private static final String TAG = "WebsocketConnectionsWorker";

    @Inject
    UserUtils userUtils;

    public WebsocketConnectionsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @SuppressLint("LongLogTag")
    @NonNull
    @Override
    public Result doWork() {
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);

        List<UserEntity> userEntityList = userUtils.getUsers();
        UserEntity userEntity;
        ExternalSignalingServer externalSignalingServer;
        WebSocketConnectionHelper webSocketConnectionHelper = new WebSocketConnectionHelper();
        for (int i = 0; i < userEntityList.size(); i++) {
            userEntity = userEntityList.get(i);
            if (!TextUtils.isEmpty(userEntity.getExternalSignalingServer())) {
                try {
                    externalSignalingServer = LoganSquare.parse(userEntity.getExternalSignalingServer(), ExternalSignalingServer.class);
                    if (!TextUtils.isEmpty(externalSignalingServer.getExternalSignalingServer()) &&
                            !TextUtils.isEmpty(externalSignalingServer.getExternalSignalingTicket())) {
                        webSocketConnectionHelper.getExternalSignalingInstanceForServer(
                                externalSignalingServer.getExternalSignalingServer(),
                                userEntity, externalSignalingServer.getExternalSignalingTicket(),
                                false);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Failed to parse external signaling server");
                }
            }
        }

        return Result.success();
    }
}
