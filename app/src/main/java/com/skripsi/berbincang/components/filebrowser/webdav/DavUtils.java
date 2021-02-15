
package com.skripsi.berbincang.components.filebrowser.webdav;

import at.bitfire.dav4android.Property;
import at.bitfire.dav4android.PropertyFactory;
import at.bitfire.dav4android.PropertyRegistry;
import at.bitfire.dav4android.property.*;
import com.skripsi.berbincang.components.filebrowser.models.properties.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DavUtils {
    public static final String OC_NAMESPACE = "http://owncloud.org/ns";
    public static final String NC_NAMESPACE = "http://nextcloud.org/ns";
    public static final String DAV_PATH = "/remote.php/dav/files/";

    public static final String EXTENDED_PROPERTY_NAME_PERMISSIONS = "permissions";
    public static final String EXTENDED_PROPERTY_NAME_REMOTE_ID = "id";
    public static final String EXTENDED_PROPERTY_NAME_SIZE = "size";
    public static final String EXTENDED_PROPERTY_FAVORITE = "favorite";
    public static final String EXTENDED_PROPERTY_IS_ENCRYPTED = "is-encrypted";
    public static final String EXTENDED_PROPERTY_MOUNT_TYPE = "mount-type";
    public static final String EXTENDED_PROPERTY_OWNER_ID = "owner-id";
    public static final String EXTENDED_PROPERTY_OWNER_DISPLAY_NAME = "owner-display-name";
    public static final String EXTENDED_PROPERTY_UNREAD_COMMENTS = "comments-unread";
    public static final String EXTENDED_PROPERTY_HAS_PREVIEW = "has-preview";
    public static final String EXTENDED_PROPERTY_NOTE = "note";
    public static final String TRASHBIN_FILENAME = "trashbin-filename";
    public static final String TRASHBIN_ORIGINAL_LOCATION = "trashbin-original-location";
    public static final String TRASHBIN_DELETION_TIME = "trashbin-deletion-time";

    public static final String PROPERTY_QUOTA_USED_BYTES = "quota-used-bytes";
    public static final String PROPERTY_QUOTA_AVAILABLE_BYTES = "quota-available-bytes";

    static Property.Name[] getAllPropSet() {
        List<Property.Name> propSet = new ArrayList<>();

        propSet.add(DisplayName.NAME);
        propSet.add(GetContentType.NAME);
        propSet.add(GetContentLength.NAME);
        propSet.add(GetContentType.NAME);
        propSet.add(GetContentLength.NAME);
        propSet.add(GetLastModified.NAME);
        propSet.add(CreationDate.NAME);
        propSet.add(GetETag.NAME);
        propSet.add(ResourceType.NAME);

        propSet.add(new Property.Name(OC_NAMESPACE, EXTENDED_PROPERTY_NAME_PERMISSIONS));
        propSet.add(OCId.NAME);
        propSet.add(OCSize.NAME);
        propSet.add(OCFavorite.NAME);
        propSet.add(new Property.Name(OC_NAMESPACE, EXTENDED_PROPERTY_OWNER_ID));
        propSet.add(new Property.Name(OC_NAMESPACE, EXTENDED_PROPERTY_OWNER_DISPLAY_NAME));
        propSet.add(new Property.Name(OC_NAMESPACE, EXTENDED_PROPERTY_UNREAD_COMMENTS));

        propSet.add(NCEncrypted.NAME);
        propSet.add(new Property.Name(NC_NAMESPACE, EXTENDED_PROPERTY_MOUNT_TYPE));
        propSet.add(NCPreview.NAME);
        propSet.add(new Property.Name(NC_NAMESPACE, EXTENDED_PROPERTY_NOTE));

        return propSet.toArray(new Property.Name[0]);
    }

    public static void registerCustomFactories() {
        PropertyRegistry propertyRegistry = PropertyRegistry.INSTANCE;
        try {
            Field factories = propertyRegistry.getClass().getDeclaredField("factories");
            factories.setAccessible(true);
            Map<Property.Name, PropertyFactory> reflectionMap = (HashMap<Property.Name,
                    PropertyFactory>) factories.get(propertyRegistry);

            reflectionMap.put(OCId.NAME, new OCId.Factory());
            reflectionMap.put(NCPreview.NAME, new NCPreview.Factory());
            reflectionMap.put(NCEncrypted.NAME, new NCEncrypted.Factory());
            reflectionMap.put(OCFavorite.NAME, new OCFavorite.Factory());
            reflectionMap.put(OCSize.NAME, new OCSize.Factory());

            factories.set(propertyRegistry, reflectionMap);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
