package com.lu.ata;

/**
 * @author Lu
 * @date 2024/12/8 21:40
 * @description 请求体
 */
public class AtaRequest extends AtaMessage {
    private String method;
    private String url;

    @Override
    public byte[] toBytes() {
        setFirstLine(method + " " + url + " " + protocol + "/" + protocolVersion);
        return super.toBytes();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProtocol() {
        return protocol;
    }



}
