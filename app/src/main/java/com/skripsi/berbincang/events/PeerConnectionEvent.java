

package com.skripsi.berbincang.events;

import androidx.annotation.Nullable;
import lombok.Data;

@Data
public class PeerConnectionEvent {
    private final PeerConnectionEventType peerConnectionEventType;
    private final String sessionId;
    private final String nick;
    private final Boolean changeValue;
    private final String videoStreamType;

    public PeerConnectionEvent(PeerConnectionEventType peerConnectionEventType, @Nullable String sessionId,
                               @Nullable String nick, Boolean changeValue, @Nullable String videoStreamType) {
        this.peerConnectionEventType = peerConnectionEventType;
        this.nick = nick;
        this.changeValue = changeValue;
        this.sessionId = sessionId;
        this.videoStreamType = videoStreamType;
    }

    public enum PeerConnectionEventType {
        PEER_CONNECTED, PEER_CLOSED, SENSOR_FAR, SENSOR_NEAR, NICK_CHANGE, AUDIO_CHANGE, VIDEO_CHANGE, PUBLISHER_FAILED
    }
}
