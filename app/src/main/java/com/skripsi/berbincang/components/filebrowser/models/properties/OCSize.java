

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

public class OCSize implements Property {
    public static final Property.Name NAME = new Property.Name(DavUtils.OC_NAMESPACE, DavUtils.EXTENDED_PROPERTY_NAME_SIZE);

    @Getter
    @Setter
    private long ocSize;

    private OCSize(long size) {
        ocSize = size;
    }

    public static class Factory implements PropertyFactory {

        @Nullable
        @Override
        public Property create(@NotNull XmlPullParser xmlPullParser) {
            try {
                String text = XmlUtils.INSTANCE.readText(xmlPullParser);
                if (!TextUtils.isEmpty(text)) {
                    return new OCSize(Long.parseLong(text));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return new OCSize(-1);
        }

        @NotNull
        @Override
        public Name getName() {
            return NAME;
        }
    }
}
