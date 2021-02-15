

package com.skripsi.berbincang.events;

import androidx.annotation.Nullable;
import lombok.Data;

import java.util.HashMap;

@Data
public class WebSocketCommunicationEvent {
    public final String type;
    @Nullable
    public final HashMap<String, String> hashMap;
}