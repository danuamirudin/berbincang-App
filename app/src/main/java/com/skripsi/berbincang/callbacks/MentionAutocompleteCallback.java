
package com.skripsi.berbincang.callbacks;

import android.content.Context;
import android.text.Editable;
import android.text.Spanned;
import android.widget.EditText;

import com.facebook.widget.text.span.BetterImageSpan;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.models.json.mention.Mention;
import com.skripsi.berbincang.utils.DisplayUtils;
import com.skripsi.berbincang.utils.MagicCharPolicy;
import com.skripsi.berbincang.utils.text.Spans;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.vanniktech.emoji.EmojiRange;
import com.vanniktech.emoji.EmojiUtils;

public class MentionAutocompleteCallback implements AutocompleteCallback<Mention> {
    private Context context;
    private UserEntity conversationUser;
    private EditText editText;

    public MentionAutocompleteCallback(Context context, UserEntity conversationUser,
                                       EditText editText) {
        this.context = context;
        this.conversationUser = conversationUser;
        this.editText = editText;
    }

    @Override
    public boolean onPopupItemClicked(Editable editable, Mention item) {
        int[] range = MagicCharPolicy.getQueryRange(editable);
        if (range == null) return false;
        int start = range[0];
        int end = range[1];
        String replacement = item.getLabel();

        StringBuilder replacementStringBuilder = new StringBuilder(item.getLabel());
        for(EmojiRange emojiRange : EmojiUtils.emojis(replacement)) {
            replacementStringBuilder.delete(emojiRange.start, emojiRange.end);
        }

        editable.replace(start, end, replacementStringBuilder.toString() + " ");
        Spans.MentionChipSpan mentionChipSpan =
                new Spans.MentionChipSpan(DisplayUtils.getDrawableForMentionChipSpan(context,
                        item.getId(), item.getLabel(), conversationUser, item.getSource(),
                        R.xml.chip_you, editText),
                        BetterImageSpan.ALIGN_CENTER,
                        item.getId(), item.getLabel());
        editable.setSpan(mentionChipSpan, start, start + replacementStringBuilder.toString().length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return true;
    }

    @Override
    public void onPopupVisibilityChanged(boolean shown) {

    }
}
