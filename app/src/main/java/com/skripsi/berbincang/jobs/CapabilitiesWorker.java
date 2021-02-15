

package com.skripsi.berbincang.jobs;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import autodagger.AutoInjector;
import com.bluelinelabs.logansquare.LoganSquare;
import com.skripsi.berbincang.api.NcApi;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.events.EventStatus;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.models.json.capabilities.CapabilitiesOverall;
import com.skripsi.berbincang.utils.ApiUtils;
import com.skripsi.berbincang.utils.bundle.BundleKeys;
import com.skripsi.berbincang.utils.database.user.UserUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.io.IOException;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

@AutoInjector(NextcloudTalkApplication.class)
public class CapabilitiesWorker extends Worker {
    public static final String TAG = "CapabilitiesWorker";

    @Inject
    UserUtils userUtils;

    @Inject
    Retrofit retrofit;

    @Inject
    EventBus eventBus;

    @Inject
    OkHttpClient okHttpClient;

    NcApi ncApi;

    public CapabilitiesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    private void updateUser(CapabilitiesOverall capabilitiesOverall, UserEntity internalUserEntity) {
        try {
            userUtils.createOrUpdateUser(null, null,
                    null, null,
                    null, null, null, internalUserEntity.getId(),
                    LoganSquare.serialize(capabilitiesOverall.getOcs().getData().getCapabilities()), null, null)
                    .blockingSubscribe(new Observer<UserEntity>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(UserEntity userEntity) {
                            eventBus.post(new EventStatus(userEntity.getId(),
                                    EventStatus.EventType.CAPABILITIES_FETCH, true));
                        }

                        @Override
                        public void onError(Throwable e) {
                            eventBus.post(new EventStatus(internalUserEntity.getId(),
                                    EventStatus.EventType.CAPABILITIES_FETCH, false));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (IOException e) {
            Log.e(TAG, "Failed to create or update user");
        }

    }

    @NonNull
    @Override
    public Result doWork() {
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);

        Data data = getInputData();

        long internalUserId = data.getLong(BundleKeys.INSTANCE.getKEY_INTERNAL_USER_ID(), -1);

        UserEntity userEntity;
        List userEntityObjectList = new ArrayList();

        if (internalUserId == -1 || (userEntity = userUtils.getUserWithInternalId(internalUserId)) == null) {
            userEntityObjectList = userUtils.getUsers();
        } else {
            userEntityObjectList.add(userEntity);
        }

        for (Object userEntityObject : userEntityObjectList) {
            UserEntity internalUserEntity = (UserEntity) userEntityObject;

            ncApi = retrofit.newBuilder().client(okHttpClient.newBuilder().cookieJar(new
                    JavaNetCookieJar(new CookieManager())).build()).build().create(NcApi.class);

            ncApi.getCapabilities(ApiUtils.getCredentials(internalUserEntity.getUsername(),
                    internalUserEntity.getToken()), ApiUtils.getUrlForCapabilities(internalUserEntity.getBaseUrl()))
                    .retry(3)
                    .blockingSubscribe(new Observer<CapabilitiesOverall>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(CapabilitiesOverall capabilitiesOverall) {
                            updateUser(capabilitiesOverall, internalUserEntity);
                        }

                        @Override
                        public void onError(Throwable e) {
                            eventBus.post(new EventStatus(internalUserEntity.getId(),
                                    EventStatus.EventType.CAPABILITIES_FETCH, false));

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        return Result.success();
    }
}
