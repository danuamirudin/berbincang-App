

package com.skripsi.berbincang.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.PatternsCompat;
import com.skripsi.berbincang.models.json.chat.ChatMessage;
import com.vanniktech.emoji.EmojiInformation;
import com.vanniktech.emoji.EmojiUtils;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import eu.medsea.mimeutil.detector.OpendesktopMimeDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextMatchers {

    private static final String TAG = "TextMatchers";

    public static ChatMessage.MessageType getMessageTypeFromString(@NonNull final String text) {
        List<String> links = new ArrayList<>();
        Matcher m = PatternsCompat.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            links.add(url);
        }

        if (links.size() == 1 && text.trim().length() == links.get(0).length()) {
            String specialLink = links.get(0);
            if (specialLink.startsWith("https://media.giphy.com/") && specialLink.endsWith(".gif")) {
                return ChatMessage.MessageType.SINGLE_LINK_GIPHY_MESSAGE;
            } else if (specialLink.contains("tenor.com/") &&
                    Pattern.compile("https://media.*\\.tenor\\.com.*\\.gif.*",
                            Pattern.CASE_INSENSITIVE).matcher(specialLink).matches()) {
                return ChatMessage.MessageType.SINGLE_LINK_TENOR_MESSAGE;
            } else {
                if (specialLink.contains("?")) {
                    specialLink = specialLink.substring(0, specialLink.indexOf("?"));
                }
                MimeUtil.registerMimeDetector(MagicMimeMimeDetector.class.getName());
                MimeUtil.registerMimeDetector(ExtensionMimeDetector.class.getName());
                MimeUtil.registerMimeDetector(OpendesktopMimeDetector.class.getName());

                String mimeType = MimeUtil.getMostSpecificMimeType(MimeUtil.getMimeTypes(specialLink)).toString();
                if (mimeType.startsWith("image/")) {
                    if (mimeType.equalsIgnoreCase("image/gif")) {
                        return ChatMessage.MessageType.SINGLE_LINK_GIF_MESSAGE;
                    } else {
                        return ChatMessage.MessageType.SINGLE_LINK_IMAGE_MESSAGE;
                    }
                } else if (mimeType.startsWith("video/")) {
                    return ChatMessage.MessageType.SINGLE_LINK_VIDEO_MESSAGE;
                } else if (mimeType.startsWith("audio/")) {
                    return ChatMessage.MessageType.SINGLE_LINK_AUDIO_MESSAGE;
                }

                return ChatMessage.MessageType.SINGLE_LINK_MESSAGE;
            }
        }

        // if we have 0 or more than 1 link, we're a regular message
        return ChatMessage.MessageType.REGULAR_TEXT_MESSAGE;
    }

    public static boolean isMessageWithSingleEmoticonOnly(@Nullable final String text) {
        final EmojiInformation emojiInformation = EmojiUtils.emojiInformation(text);
        return (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size() == 1);
    }
}
