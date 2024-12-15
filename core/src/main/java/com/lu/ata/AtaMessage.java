package com.lu.ata;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Lu
 * @date 2024/12/8 20:20
 * @description 应用到应用，消息报文，基本好http协议格式一致
 * 不完全照搬，不允许重复的请求头key
 */
public class AtaMessage {
    protected String protocol = "ata";
    protected String protocolVersion = "1.0";

    String firstLine;
    protected LinkedHashMap<String, String> mHeaders = new LinkedHashMap<>();
    protected byte[] body;

    public String getHeader(String key) {
        return this.mHeaders.get(key);
    }

    public Map<String, String> getHeaders() {
        return this.mHeaders;
    }

    public byte[] getBody() {
        return this.body;
    }

    public String getBodyString() {
        return new String(this.body);
    }

    public void setHeaders(Map<String, String> headers) {
        this.mHeaders = new LinkedHashMap<>(headers);
    }

    public void setHeader(String key, String value) {
        this.mHeaders.put(key, value);
    }

    public void setBody(byte[] body) {
        if (body == null) {
            body = new byte[0];
        }
        this.body = body;
    }

    @Override
    public String toString() {
        return new String(toBytes(), StandardCharsets.UTF_8);
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    protected final void setFirstLine(String firstLine) {
        this.firstLine = firstLine;
    }

    public byte[] toBytes() {
        StringBuilder headersText = new StringBuilder();
        for (Map.Entry<String, String> stringStringEntry : mHeaders.entrySet()) {
            String key = stringStringEntry.getKey();
            if (key == null) {
                continue;
            }
            String value = stringStringEntry.getValue();
            headersText.append(key).append(":").append(value).append("\n");
        }
        byte[] headersBytes = (firstLine + "\n" + headersText + "\n").getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[headersBytes.length + body.length];
        System.arraycopy(headersBytes, 0, result, 0, headersBytes.length);
        if (body == null) {
            return result;
        }
        System.arraycopy(body, 0, result, headersBytes.length, body.length);
        return result;
    }
}
