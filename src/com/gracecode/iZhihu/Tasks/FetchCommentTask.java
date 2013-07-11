package com.gracecode.iZhihu.Tasks;

import android.content.Context;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class FetchCommentTask extends BaseTasks<Long, Void, String> {
    private static final int LIMIT = 50;
    private static final int HTTP_STATUS_OK = 200;
    private static final String USER_AGENT = "ZhihuAppIrisRelease/103 CFNetwork/655 Darwin/14.0.0";
    private static final String AUTHORIZATION = "Bearer 844f0d72887e4f9ca2ea9e3a27248e84";
    private static final String X_APP_VERSION = "os=7.0&platform=iPhone5,1";
    private static final String ZA = "2.1.1";
    private static final int TIMEOUT_SECONDS = 5;

    public FetchCommentTask(Context context, Callback callback) {
        super(context, callback);
    }

    @Override
    protected String doInBackground(Long... answerIds) {

        for (Long answerId : answerIds) {
            HttpGet httpGet = new HttpGet(getRequestUrl(answerId));
            httpGet.addHeader("User-Agent", USER_AGENT);
            httpGet.addHeader("Authorization", AUTHORIZATION);
            httpGet.addHeader("ZA", ZA);
            httpGet.addHeader("X-APP-VERSION", X_APP_VERSION);
            httpGet.addHeader("Cache-Control", "no-cache");

            try {
                DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
                HttpParams httpParams = defaultHttpClient.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_SECONDS * 1000);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_SECONDS * 1000);

                HttpResponse httpResponse = defaultHttpClient.execute(httpGet);

                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == HTTP_STATUS_OK) {
                    return EntityUtils.toString(httpResponse.getEntity());
                } else {
                    throw new IOException(httpResponse.getStatusLine().getReasonPhrase());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(context.getPackageName(), e.getMessage());
            }
        }

        return null;
    }

    private String getRequestUrl(Long answerId) {
        return "http://api.zhihu.com/answers/" + answerId + "/comments?limit=" + LIMIT + "&offset=0";
    }
}
