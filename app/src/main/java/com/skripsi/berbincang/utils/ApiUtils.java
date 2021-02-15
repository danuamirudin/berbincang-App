
package com.skripsi.berbincang.utils;

import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.DimenRes;
import com.skripsi.berbincang.BuildConfig;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.models.RetrofitBucket;
import okhttp3.Credentials;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ApiUtils {
    private static String ocsApiVersion = "/ocs/v2.php";
    private static String spreedApiVersion = "/apps/spreed/api/v1";

    private static String userAgent = "Mozilla/5.0 (Android) Nextcloud-Talk v";

    public static String getUserAgent() {
        return userAgent + BuildConfig.VERSION_NAME;
    }

    public static String getUrlForLobbyForConversation(String baseUrl, String token) {
        return getRoom(baseUrl, token) + "/webinary/lobby";
    }

    public static String getUrlForRemovingParticipantFromConversation(String baseUrl, String roomToken, boolean isGuest) {
        String url = getUrlForParticipants(baseUrl, roomToken);

        if (isGuest) {
            url += "/guests";
        }

        return url;
    }

    public static RetrofitBucket getRetrofitBucketForContactsSearch(String baseUrl, @Nullable String searchQuery) {
        RetrofitBucket retrofitBucket = new RetrofitBucket();
        retrofitBucket.setUrl(baseUrl + ocsApiVersion + "/apps/files_sharing/api/v1/sharees");

        Map<String, String> queryMap = new HashMap<>();

        if (searchQuery == null) {
            searchQuery = "";
        }
        queryMap.put("format", "json");
        queryMap.put("search", searchQuery);
        queryMap.put("itemType", "call");

        retrofitBucket.setQueryMap(queryMap);

        return retrofitBucket;
    }

    public static String getUrlForFilePreviewWithRemotePath(String baseUrl, String remotePath, int px) {
        return baseUrl + "/index.php/core/preview.png?file="
                + Uri.encode(remotePath, "UTF-8")
                + "&x=" + px + "&y=" + px + "&a=1&mode=cover&forceIcon=1";
    }

    public static String getUrlForFilePreviewWithFileId(String baseUrl, String fileId, int px) {
        return baseUrl + "/index.php/core/preview?fileId="
                + fileId + "&x=" + px + "&y=" + px + "&a=1&mode=cover&forceIcon=1";
    }

    public static String getSharingUrl(String baseUrl) {
        return baseUrl + ocsApiVersion + "/apps/files_sharing/api/v1/shares";
    }

    public static RetrofitBucket getRetrofitBucketForContactsSearchFor14(String baseUrl, @Nullable String searchQuery) {
        RetrofitBucket retrofitBucket = getRetrofitBucketForContactsSearch(baseUrl, searchQuery);
        retrofitBucket.setUrl(baseUrl + ocsApiVersion + "/core/autocomplete/get");

        retrofitBucket.getQueryMap().put("itemId", "new");

        return retrofitBucket;
    }


    public static String getUrlForSettingNotificationlevel(String baseUrl, String token) {
        return getRoom(baseUrl, token) + "/notify";
    }

    public static String getUrlForSettingMyselfAsActiveParticipant(String baseUrl, String token) {
        return getRoom(baseUrl, token) + "/participants/active";
    }


    public static String getUrlForParticipants(String baseUrl, String token) {
        return getRoom(baseUrl, token) + "/participants";
    }

    public static String getUrlForCapabilities(String baseUrl) {
        return baseUrl + ocsApiVersion + "/cloud/capabilities";
    }

    public static String getUrlForGetRooms(String baseUrl) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/room";
    }

    public static String getRoom(String baseUrl, String token) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/room/" + token;
    }

    public static RetrofitBucket getRetrofitBucketForCreateRoom(String baseUrl, String roomType,
                                                                @Nullable String invite,
                                                                @Nullable String conversationName) {
        RetrofitBucket retrofitBucket = new RetrofitBucket();
        retrofitBucket.setUrl(baseUrl + ocsApiVersion + spreedApiVersion + "/room");
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("roomType", roomType);
        if (invite != null) {
            queryMap.put("invite", invite);
        }

        if (conversationName != null) {
            queryMap.put("roomName", conversationName);
        }

        retrofitBucket.setQueryMap(queryMap);

        return retrofitBucket;
    }

    public static RetrofitBucket getRetrofitBucketForAddParticipant(String baseUrl, String token, String user) {
        RetrofitBucket retrofitBucket = new RetrofitBucket();
        retrofitBucket.setUrl(baseUrl + ocsApiVersion + spreedApiVersion + "/room/" + token + "/participants");

        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("newParticipant", user);

        retrofitBucket.setQueryMap(queryMap);

        return retrofitBucket;

    }

    public static RetrofitBucket getRetrofitBucketForAddGroupParticipant(String baseUrl, String token, String group) {
        RetrofitBucket retrofitBucket = getRetrofitBucketForAddParticipant(baseUrl, token, group);
        retrofitBucket.getQueryMap().put("source", "groups");
        return retrofitBucket;
    }

    public static RetrofitBucket getRetrofitBucketForAddMailParticipant(String baseUrl, String token, String mail) {
        RetrofitBucket retrofitBucket = getRetrofitBucketForAddParticipant(baseUrl, token, mail);
        retrofitBucket.getQueryMap().put("source", "emails");
        return retrofitBucket;
    }

    public static String getUrlForRemoveSelfFromRoom(String baseUrl, String token) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/room/" + token + "/participants/self";
    }

    public static String getUrlForRoomVisibility(String baseUrl, String token) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/room/" + token + "/public";
    }

    public static String getUrlForCall(String baseUrl, String token) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/call/" + token;

    }

    public static String getUrlForCallPing(String baseUrl, String token) {
        return getUrlForCall(baseUrl, token) + "/ping";
    }

    public static String getUrlForChat(String baseUrl, String token) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/chat/" + token;
    }

    public static String getUrlForExternalServerAuthBackend(String baseUrl) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/signaling/backend";
    }

    public static String getUrlForMentionSuggestions(String baseUrl, String token) {
        return getUrlForChat(baseUrl, token) + "/mentions";
    }

    public static String getUrlForSignaling(String baseUrl, @Nullable String token) {
        String signalingUrl = baseUrl + ocsApiVersion + spreedApiVersion + "/signaling";
        if (token == null) {
            return signalingUrl;
        } else {
            return signalingUrl + "/" + token;
        }
    }

    public static String getUrlForModerators(String baseUrl, String roomToken) {
        return getRoom(baseUrl, roomToken) + "/moderators";
    }

    public static String getUrlForSignalingSettings(String baseUrl) {
        return getUrlForSignaling(baseUrl, null) + "/settings";
    }


    public static String getUrlForUserProfile(String baseUrl) {
        return baseUrl + ocsApiVersion + "/cloud/user";
    }

    public static String getUrlPostfixForStatus() {
        return "/status.php";
    }

    public static String getUrlForAvatarWithNameAndPixels(String baseUrl, String name, int avatarSize) {
        return baseUrl + "/index.php/avatar/" + Uri.encode(name) + "/" + avatarSize;
    }

    public static String getUrlForAvatarWithName(String baseUrl, String name, @DimenRes int avatarSize) {
        avatarSize = Math.round(NextcloudTalkApplication
                .Companion.getSharedApplication().getResources().getDimension(avatarSize));

        return baseUrl + "/index.php/avatar/" + Uri.encode(name) + "/" + avatarSize;
    }

    public static String getUrlForAvatarWithNameForGuests(String baseUrl, String name,
                                                  @DimenRes int avatarSize) {
        avatarSize = Math.round(NextcloudTalkApplication
                .Companion.getSharedApplication().getResources().getDimension(avatarSize));

        return baseUrl + "/index.php/avatar/guest/" + Uri.encode(name) + "/" + avatarSize;
    }

    public static String getUrlForPassword(String baseUrl, String token) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/room/" + token + "/password";
    }

    public static String getCredentials(String username, String token) {
        if (TextUtils.isEmpty(username) && TextUtils.isEmpty(token)) {
            return null;
        }
        return Credentials.basic(username, token);
    }

    public static String getUrlNextcloudPush(String baseUrl) {
        return baseUrl + ocsApiVersion + "/apps/notifications/api/v2/push";
    }

    public static String getUrlPushProxy() {
        return NextcloudTalkApplication.Companion.getSharedApplication().
                getApplicationContext().getResources().getString(R.string.nc_push_server_url) + "/devices";
    }

    public static String getUrlForConversationFavorites(String baseUrl, String roomToken) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/room/" + roomToken + "/favorite";
    }

    public static String getUrlForNotificationWithId(String baseUrl, String notificationId) {
        return baseUrl + ocsApiVersion + "/apps/notifications/api/v2/notifications/" + notificationId;
    }

    public static String getUrlForReadOnlyState(String baseUrl, String roomToken) {
        return baseUrl + ocsApiVersion + spreedApiVersion + "/room/" + roomToken + "/read-only";
    }
}
