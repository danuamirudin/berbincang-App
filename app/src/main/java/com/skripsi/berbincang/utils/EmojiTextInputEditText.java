

package com.skripsi.berbincang.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.emoji.widget.EmojiEditTextHelper;

import com.google.android.material.textfield.TextInputEditText;

public class EmojiTextInputEditText extends TextInputEditText {
    private EmojiEditTextHelper emojiEditTextHelper;

    public EmojiTextInputEditText(Context context) {
        super(context);
        init();
    }

    public EmojiTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmojiTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        super.setKeyListener(getEmojiEditTextHelper().getKeyListener(getKeyListener()));
    }

    @Override
    public void setKeyListener(android.text.method.KeyListener keyListener) {
        super.setKeyListener(getEmojiEditTextHelper().getKeyListener(keyListener));
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
        return getEmojiEditTextHelper().onCreateInputConnection(inputConnection, outAttrs);
    }

    private EmojiEditTextHelper getEmojiEditTextHelper() {
       if (emojiEditTextHelper == null) {
           emojiEditTextHelper = new EmojiEditTextHelper(this);
       }

       return emojiEditTextHelper;
    }
}
