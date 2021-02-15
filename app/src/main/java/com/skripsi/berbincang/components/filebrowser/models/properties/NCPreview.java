

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

public class NCPreview implements Property {
    public static final Property.Name NAME = new Property.Name(DavUtils.NC_NAMESPACE, DavUtils.EXTENDED_PROPERTY_HAS_PREVIEW);

    @Getter
    @Setter
    private boolean ncPreview;

    private NCPreview(boolean hasPreview) {
        ncPreview = hasPreview;
    }

    public static class Factory implements PropertyFactory {

        @Nullable
        @Override
        public Property create(@NotNull XmlPullParser xmlPullParser) {
            try {
                String text = XmlUtils.INSTANCE.readText(xmlPullParser);
                if (!TextUtils.isEmpty(text)) {
                    return new NCPreview(Boolean.parseBoolean(text));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return new OCFavorite(false);
        }

        @NotNull
        @Override
        public Property.Name getName() {
            return NAME;
        }
    }
}
