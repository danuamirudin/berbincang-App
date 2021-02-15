

package com.skripsi.berbincang.utils;

import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher;
import okhttp3.Call;
import okhttp3.OkHttpClient;

import java.util.concurrent.Executor;

public class OkHttpNetworkFetcherWithCache extends OkHttpNetworkFetcher {
    public OkHttpNetworkFetcherWithCache(OkHttpClient okHttpClient) {
        super(okHttpClient);
    }

    public OkHttpNetworkFetcherWithCache(Call.Factory callFactory, Executor cancellationExecutor) {
        super(callFactory, cancellationExecutor);
    }

    public OkHttpNetworkFetcherWithCache(Call.Factory callFactory, Executor cancellationExecutor, boolean disableOkHttpCache) {
        super(callFactory, cancellationExecutor, true);
    }
}
