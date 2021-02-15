

package com.skripsi.berbincang.components.filebrowser.models.properties;

import android.text.TextUtils;
import at.bitfire.dav4android.Property;
import at.bitfire.dav4android.PropertyFactory;
import at.bitfire.dav4android.XmlUtils;
import com.skripsi.berbincang.components.filebrowser.webdav.DavUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class NCEncrypted implements Property {
    public static final Name NAME = new Name(DavUtils.NC_NAMESPACE, DavUtils.EXTENDED_PROPERTY_IS_ENCRYPTED);

    @Getter
    @Setter
    private boolean ncEncrypted;

    private NCEncrypted(boolean isEncrypted) {
        ncEncrypted = isEncrypted;
    }

    public static class Factory implements PropertyFactory {

        @Nullable
        @Override
        public Property create(@NotNull XmlPullParser xmlPullParser) {
            try {
                String text = XmlUtils.INSTANCE.readText(xmlPullParser);
                if (!TextUtils.isEmpty(text)) {
                    return new NCEncrypted(Boolean.parseBoolean(text));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return new NCEncrypted(false);
        }

        @NotNull
        @Override
        public Name getName() {
            return NAME;
        }
    }
}
