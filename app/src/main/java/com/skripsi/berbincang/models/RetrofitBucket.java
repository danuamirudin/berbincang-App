
package com.skripsi.berbincang.models;

import lombok.Data;
import org.parceler.Parcel;

import java.util.Map;

@Parcel
@Data
public class RetrofitBucket {
    public String url;
    public Map<String, String> queryMap;
}
