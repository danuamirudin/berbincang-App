
package com.skripsi.berbincang.events;

import androidx.annotation.Nullable;
import lombok.Data;
import org.webrtc.MediaStream;

@Data
public class MediaStreamEvent {
    private final MediaStream mediaStream;
    private final String session;
    private final String videoStreamType;

    public MediaStreamEvent(@Nullable MediaStream mediaStream, String session, String videoStreamType) {
        this.mediaStream = mediaStream;
        this.session = session;
        this.videoStreamType = videoStreamType;
    }
}
