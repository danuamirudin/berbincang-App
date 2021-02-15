

package com.skripsi.berbincang.events;

import lombok.Data;

@Data
public class NetworkEvent {
    public enum NetworkConnectionEvent {
        NETWORK_CONNECTED, NETWORK_DISCONNECTED
    }

    private final NetworkConnectionEvent networkConnectionEvent;

    public NetworkEvent(NetworkConnectionEvent networkConnectionEvent) {
        this.networkConnectionEvent = networkConnectionEvent;
    }
}
