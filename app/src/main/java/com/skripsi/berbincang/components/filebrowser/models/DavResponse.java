
package com.skripsi.berbincang.components.filebrowser.models;

import at.bitfire.dav4android.Response;
import lombok.Data;

@Data
public class DavResponse {
    Response response;
    Object data;
}
