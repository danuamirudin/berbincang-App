/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * Copyright (C) 2017 Mario Danic <mario@lovelyhq.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.skripsi.berbincang.services.firebase;

import android.annotation.SuppressLint;

import autodagger.AutoInjector;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.jobs.NotificationWorker;
import com.skripsi.berbincang.jobs.PushRegistrationWorker;
import com.skripsi.berbincang.utils.bundle.BundleKeys;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.skripsi.berbincang.utils.preferences.AppPreferences;

import javax.inject.Inject;

@AutoInjector(NextcloudTalkApplication.class)
public class MagicFirebaseMessagingService extends FirebaseMessagingService {
    @Inject
    AppPreferences appPreferences;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);
        appPreferences.setPushToken(token);
        OneTimeWorkRequest pushRegistrationWork = new OneTimeWorkRequest.Builder(PushRegistrationWorker.class).build();
        WorkManager.getInstance().enqueue(pushRegistrationWork);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) {
            return;
        }

        if (remoteMessage.getData() != null) {
            Data messageData = new Data.Builder()
                    .putString(BundleKeys.INSTANCE.getKEY_NOTIFICATION_SUBJECT(), remoteMessage.getData().get("subject"))
                    .putString(BundleKeys.INSTANCE.getKEY_NOTIFICATION_SIGNATURE(), remoteMessage.getData().get("signature"))
                    .build();

            OneTimeWorkRequest pushNotificationWork = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                    .setInputData(messageData)
                    .build();
            WorkManager.getInstance().enqueue(pushNotificationWork);
        }
    }
}
