package com.lu.ata;

/**
 * @author Lu
 * @date 2024/12/8 22:02
 * @description 响应体
 */
public class AtaResponse extends AtaMessage {
    private String statusCode = "200";
    private String statusText = "OK";

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    @Override
    public byte[] toBytes() {
        setFirstLine(protocol + "/" + protocolVersion + " " + statusCode + " " + statusText);
        return super.toBytes();
    }
}
