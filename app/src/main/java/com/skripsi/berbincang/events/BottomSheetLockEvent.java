

package com.skripsi.berbincang.events;

import lombok.Data;

@Data
public class BottomSheetLockEvent {
    private final boolean cancelable;
    private final int delay;
    private final boolean shouldRefreshData;
    private final boolean cancel;
    private boolean dismissView;

    public BottomSheetLockEvent(boolean cancelable, int delay, boolean shouldRefreshData, boolean cancel) {
        this.cancelable = cancelable;
        this.delay = delay;
        this.shouldRefreshData = shouldRefreshData;
        this.cancel = cancel;
        this.dismissView = true;
    }

    public BottomSheetLockEvent(boolean cancelable, int delay, boolean shouldRefreshData, boolean cancel, boolean
            dismissView) {
        this.cancelable = cancelable;
        this.delay = delay;
        this.shouldRefreshData = shouldRefreshData;
        this.cancel = cancel;
        this.dismissView = dismissView;
    }

}
