package com.skripsi.berbincang.dagger.modules;

import android.content.Context;
import androidx.annotation.NonNull;
import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    private final Context context;

    public ContextModule(@NonNull final Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }
}
