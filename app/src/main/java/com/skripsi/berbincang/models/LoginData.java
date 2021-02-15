
package com.skripsi.berbincang.models;

import lombok.Data;
import org.parceler.Parcel;

@Parcel
@Data
public class LoginData {
    String serverUrl;
    String username;
    String token;
}
