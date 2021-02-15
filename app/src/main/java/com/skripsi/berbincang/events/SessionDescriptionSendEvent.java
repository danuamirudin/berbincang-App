

package com.skripsi.berbincang.events;

import androidx.annotation.Nullable;
import com.skripsi.berbincang.models.json.signaling.NCIceCandidate;
import lombok.Data;
import org.webrtc.SessionDescription;

@Data
public class SessionDescriptionSendEvent {
    @Nullable
    private final SessionDescription sessionDescription;
    private final String peerId;
    private final String type;
    @Nullable
    private final NCIceCandidate ncIceCandidate;
    private final String videoStreamType;

    public SessionDescriptionSendEvent(@Nullable SessionDescription sessionDescription, String peerId, String type,
                                       @Nullable NCIceCandidate ncIceCandidate, @Nullable String videoStreamType) {
        this.sessionDescription = sessionDescription;
        this.peerId = peerId;
        this.type = type;
        this.ncIceCandidate = ncIceCandidate;
        this.videoStreamType = videoStreamType;
    }
}
