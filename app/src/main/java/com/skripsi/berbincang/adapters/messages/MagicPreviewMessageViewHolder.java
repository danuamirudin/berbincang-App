
package com.skripsi.berbincang.adapters.messages;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import androidx.emoji.widget.EmojiTextView;

import autodagger.AutoInjector;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.application.NextcloudTalkApplication;
import com.skripsi.berbincang.components.filebrowser.models.BrowserFile;
import com.skripsi.berbincang.components.filebrowser.models.DavResponse;
import com.skripsi.berbincang.components.filebrowser.webdav.ReadFilesystemOperation;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.models.json.chat.ChatMessage;
import com.skripsi.berbincang.utils.AccountUtils;
import com.skripsi.berbincang.utils.DisplayUtils;
import com.skripsi.berbincang.utils.DrawableUtils;
import com.skripsi.berbincang.utils.bundle.BundleKeys;
import com.stfalcon.chatkit.messages.MessageHolders;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;

@AutoInjector(NextcloudTalkApplication.class)
public class MagicPreviewMessageViewHolder extends MessageHolders.IncomingImageMessageViewHolder<ChatMessage> {

    @BindView(R.id.messageText)
    EmojiTextView messageText;

    @Inject
    Context context;

    @Inject
    OkHttpClient okHttpClient;

    public MagicPreviewMessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        NextcloudTalkApplication.Companion.getSharedApplication().getComponentApplication().inject(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBind(ChatMessage message) {
        super.onBind(message);
        if (userAvatar != null) {
            if (message.isGrouped || message.isOneToOneConversation()) {
                if (message.isOneToOneConversation()) {
                    userAvatar.setVisibility(View.GONE);
                } else {
                    userAvatar.setVisibility(View.INVISIBLE);
                }
            } else {
                userAvatar.setVisibility(View.VISIBLE);

                if ("bots".equals(message.getActorType()) && "changelog".equals(message.getActorId())) {
                    Drawable[] layers = new Drawable[2];
                    layers[0] = context.getDrawable(R.drawable.ic_launcher_background);
                    layers[1] = context.getDrawable(R.drawable.ic_launcher_foreground);
                    LayerDrawable layerDrawable = new LayerDrawable(layers);

                    userAvatar.getHierarchy().setPlaceholderImage(DisplayUtils.getRoundedDrawable(layerDrawable));
                }
            }
        }

        if (message.getMessageType() == ChatMessage.MessageType.SINGLE_NC_ATTACHMENT_MESSAGE) {
            // it's a preview for a Nextcloud share
            messageText.setText(message.getSelectedIndividualHashMap().get("name"));
            DisplayUtils.setClickableString(message.getSelectedIndividualHashMap().get("name"), message.getSelectedIndividualHashMap().get("link"), messageText);
            if (message.getSelectedIndividualHashMap().containsKey("mimetype")) {
                image.getHierarchy().setPlaceholderImage(context.getDrawable(DrawableUtils.INSTANCE.getDrawableResourceIdForMimeType(message.getSelectedIndividualHashMap().get("mimetype"))));
            } else {
                fetchFileInformation("/" + message.getSelectedIndividualHashMap().get("path"), message.getActiveUser());
            }

            image.setOnClickListener(v -> {

                String accountString =
                        message.getActiveUser().getUsername() + "@" + message.getActiveUser().getBaseUrl().replace("https://", "").replace("http://", "");

                if (AccountUtils.INSTANCE.canWeOpenFilesApp(context, accountString)) {
                    Intent filesAppIntent = new Intent(Intent.ACTION_VIEW, null);
                    final ComponentName componentName = new ComponentName(context.getString(R.string.nc_import_accounts_from), "com.owncloud.android.ui.activity.FileDisplayActivity");
                    filesAppIntent.setComponent(componentName);
                    filesAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    filesAppIntent.setPackage(context.getString(R.string.nc_import_accounts_from));
                    filesAppIntent.putExtra(BundleKeys.INSTANCE.getKEY_ACCOUNT(), accountString);
                    filesAppIntent.putExtra(BundleKeys.INSTANCE.getKEY_FILE_ID(), message.getSelectedIndividualHashMap().get("id"));
                    context.startActivity(filesAppIntent);
                } else {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getSelectedIndividualHashMap().get("link")));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            });
        } else if (message.getMessageType() == ChatMessage.MessageType.SINGLE_LINK_GIPHY_MESSAGE) {
            messageText.setText("GIPHY");
            DisplayUtils.setClickableString("GIPHY", "https://giphy.com", messageText);
        } else if (message.getMessageType() == ChatMessage.MessageType.SINGLE_LINK_TENOR_MESSAGE) {
            messageText.setText("Tenor");
            DisplayUtils.setClickableString("Tenor", "https://tenor.com", messageText);
        } else {
            if (message.getMessageType().equals(ChatMessage.MessageType.SINGLE_LINK_IMAGE_MESSAGE)) {
                image.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getImageUrl()));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                });
            } else {
                image.setOnClickListener(null);
            }
            messageText.setText("");
        }
    }

    private void fetchFileInformation(String url, UserEntity activeUser) {
        Single.fromCallable(new Callable<ReadFilesystemOperation>() {
            @Override
            public ReadFilesystemOperation call() {
                return new ReadFilesystemOperation(okHttpClient, activeUser, url, 0);
            }
        }).observeOn(Schedulers.io())
                .subscribe(new SingleObserver<ReadFilesystemOperation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(ReadFilesystemOperation readFilesystemOperation) {
                        DavResponse davResponse = readFilesystemOperation.readRemotePath();
                        if (davResponse.getData() != null) {
                            List<BrowserFile> browserFileList = (List<BrowserFile>) davResponse.getData();
                            if (!browserFileList.isEmpty()) {
                                new Handler(context.getMainLooper()).post(() -> image.getHierarchy().setPlaceholderImage(context.getDrawable(DrawableUtils.INSTANCE.getDrawableResourceIdForMimeType(browserFileList.get(0).getMimeType()))));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });

    }
}
