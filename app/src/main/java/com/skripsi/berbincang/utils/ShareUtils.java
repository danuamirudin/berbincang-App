

package com.skripsi.berbincang.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.kennyc.bottomsheet.adapters.AppAdapter;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.models.json.conversations.Conversation;
import com.skripsi.berbincang.utils.database.user.UserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShareUtils {

    public static String getStringForIntent(Context context, @Nullable String password, UserUtils userUtils, Conversation
            conversation) {
        UserEntity userEntity = userUtils.getCurrentUser();

        String shareString = "";
        if (userEntity != null && context != null) {
            shareString = String.format(context.getResources().getString(R.string.nc_share_text),
                    userEntity.getBaseUrl(), conversation.getToken());

            if (!TextUtils.isEmpty(password)) {
                shareString += String.format(context.getResources().getString(R.string.nc_share_text_pass), password);
            }
        }

        return shareString;
    }

    public static List<AppAdapter.AppInfo> getShareApps(Context context, Intent intent,
                                                        @Nullable Set<String> appsFilter, @Nullable Set<String> toExclude) {

        if (context == null || intent == null) return null;

        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);

        if (apps != null && !apps.isEmpty()) {
            List<AppAdapter.AppInfo> appResources = new ArrayList<>(apps.size());
            boolean shouldCheckPackages = appsFilter != null && !appsFilter.isEmpty();

            for (ResolveInfo resolveInfo : apps) {
                String packageName = resolveInfo.activityInfo.packageName;

                if (shouldCheckPackages && !appsFilter.contains(packageName)) {
                    continue;
                }

                String title = resolveInfo.loadLabel(manager).toString();
                String name = resolveInfo.activityInfo.name;
                Drawable drawable = resolveInfo.loadIcon(manager);
                appResources.add(new AppAdapter.AppInfo(title, packageName, name, drawable));
            }

            if (toExclude != null && !toExclude.isEmpty()) {
                List<AppAdapter.AppInfo> toRemove = new ArrayList<>();

                for (AppAdapter.AppInfo appInfo : appResources) {
                    if (toExclude.contains(appInfo.packageName)) {
                        toRemove.add(appInfo);
                    }
                }

                if (!toRemove.isEmpty()) appResources.removeAll(toRemove);
            }

            return appResources;

        }

        return null;
    }

}
