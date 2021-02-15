

package com.skripsi.berbincang.models;

import androidx.annotation.Nullable;
import lombok.Data;

@Data
public class ImportAccount {
    public String username;
    @Nullable
    public String token;
    public String baseUrl;

    public ImportAccount(String username, @Nullable String token, String baseUrl) {
        this.username = username;
        this.token = token;
        this.baseUrl = baseUrl;
    }
}
