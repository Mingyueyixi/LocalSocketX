package com.lu.ata;

/**
 * @author Lu
 * @date 2024/12/8 23:52
 * @description
 */
public class AtaCall {
    private final String mUrl;
    private final String mMethod;
    private final byte[] mBody;
    private AtaClient mAtaClient;

    public AtaCall(AtaClient ataClient, String url, String method, byte[] body) {
        mAtaClient = ataClient;
        mUrl = url;
        mMethod = method;
        mBody = body;
    }

    public void execute(OnResponseListener listener) {
        AtaExecutors.executeNetwork(() -> {
            AtaRequest request = new AtaRequest();
            request.setUrl(mUrl);
            request.setMethod(mMethod);
            request.setBody(mBody);
            AtaResponse ataResponse = null;
            Exception error = null;
            try {
                ataResponse = mAtaClient.request(request);
            } catch (Exception e) {
                e.printStackTrace();
                error = e;
            }
            AtaResponse finalAtaResponse = ataResponse;
            Exception finalError = error;
            AtaExecutors.executeMain(() -> {
                if (listener != null) {
                    if (finalError == null) {
                        listener.onSuccess(finalAtaResponse);
                    } else {
                        listener.onFailure(finalError);
                    }
                }

            });
        });

    }

    public static class Builder {
        private AtaClient client;
        private String path;
        private String method;
        private byte[] body;

        public Builder(AtaClient client) {
            this.client = client;
        }

        public Builder setUrl(String path) {
            this.path = path;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public void execute(OnResponseListener listener) {
            new AtaCall(client, path, method, body).execute(listener);
        }
    }
}
