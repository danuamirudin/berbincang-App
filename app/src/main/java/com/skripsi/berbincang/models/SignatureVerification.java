

package com.skripsi.berbincang.models;


import com.skripsi.berbincang.models.database.UserEntity;
import lombok.Data;
import org.parceler.Parcel;

@Data
@Parcel
public class SignatureVerification {
    boolean signatureValid;
    UserEntity userEntity;
}
