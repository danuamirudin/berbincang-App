

package com.skripsi.berbincang.components.filebrowser.webdav;

import at.bitfire.dav4android.DavResource;
import at.bitfire.dav4android.Response;
import at.bitfire.dav4android.exception.DavException;
import com.skripsi.berbincang.components.filebrowser.models.BrowserFile;
import com.skripsi.berbincang.components.filebrowser.models.DavResponse;
import com.skripsi.berbincang.dagger.modules.RestModule;
import com.skripsi.berbincang.models.database.UserEntity;
import com.skripsi.berbincang.utils.ApiUtils;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadFilesystemOperation {
    private final OkHttpClient okHttpClient;
    private final String url;
    private final int depth;
    private final String basePath;

    public ReadFilesystemOperation(OkHttpClient okHttpClient, UserEntity currentUser, String path, int depth) {
        OkHttpClient.Builder okHttpClientBuilder = okHttpClient.newBuilder();
        okHttpClientBuilder.followRedirects(false);
        okHttpClientBuilder.followSslRedirects(false);
        okHttpClientBuilder.authenticator(new RestModule.MagicAuthenticator(ApiUtils.getCredentials(currentUser.getUsername(), currentUser.getToken()), "Authorization"));
        this.okHttpClient = okHttpClientBuilder.build();
        this.basePath = currentUser.getBaseUrl() + DavUtils.DAV_PATH + currentUser.getUserId();
        this.url = basePath + path;
        this.depth = depth;
    }

    public DavResponse readRemotePath() {
        DavResponse davResponse = new DavResponse();
        final List<Response> memberElements = new ArrayList<>();
        final Response[] rootElement = new Response[1];
        final List<BrowserFile> remoteFiles = new ArrayList<>();

        try {
            new DavResource(okHttpClient, HttpUrl.parse(url)).propfind(depth, DavUtils.getAllPropSet(),
                    new Function2<Response, Response.HrefRelation, Unit>() {
                        @Override
                        public Unit invoke(Response response, Response.HrefRelation hrefRelation) {
                            davResponse.setResponse(response);
                            switch (hrefRelation) {
                                case MEMBER:
                                    memberElements.add(response);
                                    break;
                                case SELF:
                                    rootElement[0] = response;
                                    break;
                                case OTHER:
                                default:
                            }
                            return Unit.INSTANCE;
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DavException e) {
            e.printStackTrace();
        }


        remoteFiles.add(BrowserFile.getModelFromResponse(rootElement[0],
                rootElement[0].getHref().toString().substring(basePath.length())));
        for (Response memberElement : memberElements) {
            remoteFiles.add(BrowserFile.getModelFromResponse(memberElement,
                    memberElement.getHref().toString().substring(basePath.length())));
        }

        davResponse.setData(remoteFiles);
        return davResponse;
    }

}
