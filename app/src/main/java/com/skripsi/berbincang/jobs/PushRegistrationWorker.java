
package com.skripsi.berbincang.jobs;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.skripsi.berbincang.utils.PushUtils;

public class PushRegistrationWorker extends Worker {
    public static final String TAG = "PushRegistrationWorker";

    public PushRegistrationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        PushUtils pushUtils = new PushUtils();
        pushUtils.generateRsa2048KeyPair();
        pushUtils.pushRegistrationToServer();

        return Result.success();
    }
}
