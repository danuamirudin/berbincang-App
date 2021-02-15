

package com.skripsi.berbincang.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import lombok.Data;
import org.parceler.Parcel;

@Data
@Parcel
@JsonObject
public class ExternalSignalingServer {
    @JsonField(name = "externalSignalingServer")
    String externalSignalingServer;
    @JsonField(name = "externalSignalingTicket")
    String externalSignalingTicket;
}
