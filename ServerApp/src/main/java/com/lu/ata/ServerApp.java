package com.lu.ata;

import android.app.Application;

import com.lu.ata.AtaConnection;
import com.lu.ata.AtaRequest;
import com.lu.ata.AtaResponse;
import com.lu.ata.AtaServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Lu
 * @date 2024/12/15 15:52
 * @description
 */
public class ServerApp extends Application {
    private static final String SOCKET_NAME = "ata";
    private AtaServer mAtaServer;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    @Override
    public void onCreate() {
        super.onCreate();
        initAtaServer();
    }

    private void initAtaServer() {
        mAtaServer = new AtaServer();
        mAtaServer.startServer(SOCKET_NAME);
        mAtaServer.setOnListener(new AtaServer.OnListener() {
            @Override
            public AtaResponse onRequest(AtaRequest request) {
                String url = request.getUrl();
                HashMap<String, Object> map = new HashMap<>();
                map.put("url", url);
                map.put("protocol", request.getProtocol() + "/" + request.getProtocolVersion());
                map.put("method", request.getMethod());
                map.put("headers", request.getHeaders());
                map.put("body", request.getBodyString());
                AtaResponse resp = null;
                try {
                    resp = response(new JSONObject(map).toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ServerletUtil.getInstance().dispatchOnRequest(request, resp);
                return resp;
            }

            private AtaResponse response(String s) {
                AtaResponse resp = new AtaResponse();
                resp.setHeader("Content-Type", "application/bin");
                resp.setHeader("Date", mDateFormat.format(new Date()));
                resp.setBody(s.getBytes());
                return resp;
            }

            @Override
            public void onError(AtaConnection session, Throwable error) {

            }

            @Override
            public void onCloseSession(AtaConnection session) {

            }

            @Override
            public void onStopServer() {

            }
        });
    }

}
