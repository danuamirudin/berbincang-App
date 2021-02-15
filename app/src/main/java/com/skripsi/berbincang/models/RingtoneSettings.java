

package com.skripsi.berbincang.models;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.skripsi.berbincang.models.json.converters.UriTypeConverter;
import lombok.Data;
import org.parceler.Parcel;

@Parcel
@JsonObject
@Data
public class RingtoneSettings {
    @JsonField(name = "ringtoneUri", typeConverter = UriTypeConverter.class)
    @Nullable
    Uri ringtoneUri;
    @JsonField(name = "ringtoneName")
    String ringtoneName;
}
