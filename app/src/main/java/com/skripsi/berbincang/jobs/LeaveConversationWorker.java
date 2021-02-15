

package com.skripsi.berbincang.jobs;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import autodagger.AutoInjector;
import com.skripsi.berbincang.api.NcApi;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.events.EventStatus;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.models.json.generic.GenericOverall;
import com.skripsi.berbincang.utils.ApiUtils;
import com.skripsi.berbincang.utils.bundle.BundleKeys;
import com.skripsi.berbincang.utils.database.user.UserUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.net.CookieManager;

@AutoInjector(NextcloudTalkApplication.class)
public class LeaveConversationWorker extends Worker {
    @Inject
    Retrofit retrofit;

    @Inject
    OkHttpClient okHttpClient;

    @Inject
    UserUtils userUtils;

    @Inject
    EventBus eventBus;

    NcApi ncApi;

    public LeaveConversationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        long operationUserId = data.getLong(BundleKeys.INSTANCE.getKEY_INTERNAL_USER_ID(), -1);
        String conversationToken = data.getString(BundleKeys.INSTANCE.getKEY_ROOM_TOKEN());
        UserEntity operationUser = userUtils.getUserWithId(operationUserId);

        if (operationUser != null) {
            String credentials = ApiUtils.getCredentials(operationUser.getUsername(), operationUser.getToken());
            ncApi = retrofit.newBuilder().client(okHttpClient.newBuilder().cookieJar(new
                    JavaNetCookieJar(new CookieManager())).build()).build().create(NcApi.class);

            EventStatus eventStatus = new EventStatus(operationUser.getId(),
                    EventStatus.EventType.CONVERSATION_UPDATE, true);

            ncApi.removeSelfFromRoom(credentials, ApiUtils.getUrlForRemoveSelfFromRoom(operationUser.getBaseUrl(), conversationToken))
                    .subscribeOn(Schedulers.io())
                    .blockingSubscribe(new Observer<GenericOverall>() {
                        Disposable disposable;

                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;

                        }

                        @Override
                        public void onNext(GenericOverall genericOverall) {
                            eventBus.postSticky(eventStatus);

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            disposable.dispose();
                        }
                    });
        }

        return Result.success();
    }
}
