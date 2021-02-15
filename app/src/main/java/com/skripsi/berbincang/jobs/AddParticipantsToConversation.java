

package com.skripsi.berbincang.jobs;

import android.content.Context;

import com.skripsi.berbincang.api.NcApi;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.events.EventStatus;
import com.skripsi.berbincang.models.RetrofitBucket;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.utils.ApiUtils;
import com.skripsi.berbincang.utils.bundle.BundleKeys;
import com.skripsi.berbincang.utils.database.user.UserUtils;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import autodagger.AutoInjector;
import io.reactivex.schedulers.Schedulers;

@AutoInjector(NextcloudTalkApplication.class)
public class AddParticipantsToConversation extends Worker {
    @Inject
    NcApi ncApi;

    @Inject
    UserUtils userUtils;

    @Inject
    EventBus eventBus;

    public AddParticipantsToConversation(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        String[] selectedUserIds = data.getStringArray(BundleKeys.INSTANCE.getKEY_SELECTED_USERS());
        String[] selectedGroupIds = data.getStringArray(BundleKeys.INSTANCE.getKEY_SELECTED_GROUPS());
        UserEntity user = userUtils.getUserWithInternalId(data.getLong(BundleKeys.INSTANCE.getKEY_INTERNAL_USER_ID(), -1));
        String conversationToken = data.getString(BundleKeys.INSTANCE.getKEY_TOKEN());
        String credentials = ApiUtils.getCredentials(user.getUsername(), user.getToken());

        RetrofitBucket retrofitBucket;
        for (String userId : selectedUserIds) {
            retrofitBucket = ApiUtils.getRetrofitBucketForAddParticipant(user.getBaseUrl(), conversationToken,
                    userId);

            ncApi.addParticipant(credentials, retrofitBucket.getUrl(), retrofitBucket.getQueryMap())
                    .subscribeOn(Schedulers.io())
                    .blockingSubscribe();
        }

        for (String groupId : selectedGroupIds) {
            retrofitBucket = ApiUtils.getRetrofitBucketForAddGroupParticipant(user.getBaseUrl(), conversationToken,
                    groupId);

            ncApi.addParticipant(credentials, retrofitBucket.getUrl(), retrofitBucket.getQueryMap())
                    .subscribeOn(Schedulers.io())
                    .blockingSubscribe();
        }

        eventBus.post(new EventStatus(user.getId(), EventStatus.EventType.PARTICIPANTS_UPDATE, true));
        return Result.success();
    }
}
