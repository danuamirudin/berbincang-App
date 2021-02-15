
package com.skripsi.berbincang.dagger.modules;

import android.content.Context;
import androidx.annotation.NonNull;
import com.skripsi.berbincang.R;
import com.skripsi.berbincang.models.database.Models;
import com.skripsi.berbincang.utils.preferences.AppPreferences;
import dagger.Module;
import dagger.Provides;
import io.requery.Persistable;
import io.requery.android.sqlcipher.SqlCipherDatabaseSource;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;
import net.orange_box.storebox.StoreBox;

import javax.inject.Singleton;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public SqlCipherDatabaseSource provideSqlCipherDatabaseSource(@NonNull final Context context) {
        return new SqlCipherDatabaseSource(context, Models.DEFAULT,
                context.getResources().getString(R.string.nc_app_name).toLowerCase()
                        .replace(" ", "_").trim() + ".sqlite",
                context.getString(R.string.nc_talk_database_encryption_key), 6);
    }

    @Provides
    @Singleton
    public ReactiveEntityStore<Persistable> provideDataStore(@NonNull final SqlCipherDatabaseSource sqlCipherDatabaseSource) {
        final Configuration configuration = sqlCipherDatabaseSource.getConfiguration();
        return ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));
    }

    @Provides
    @Singleton
    public AppPreferences providePreferences(@NonNull final Context poContext) {
        return StoreBox.create(poContext, AppPreferences.class);
    }
}
