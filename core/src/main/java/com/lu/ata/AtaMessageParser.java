package com.lu.ata;

import java.util.LinkedHashMap;

/**
 * @author Lu
 * @date 2024/12/8 21:49
 * @description
 */
public class AtaMessageParser {

    /**
     * 解析请求报文为实体类
     */
    public static AtaRequest parseRequest(byte[] bin) {
        if (bin == null) {
            return null;
        }
        AtaRequest result = new AtaRequest();
        AtaMessage ataMessage = parse(bin);
        String line = ataMessage.firstLine;
        String[] parts = line.split(" ");
        if (parts.length > 0) {
            result.setMethod(parts[0]);
        }
        if (parts.length > 1) {
            result.setUrl(parts[1]);
        }
        if (parts.length > 2) {
            String protocolText = parts[2];
            String[] p = protocolText.split("/");
            result.setProtocol(p[0]);
            result.setProtocolVersion(p[1]);
        }
        result.setHeaders(ataMessage.getHeaders());
        result.setBody(ataMessage.getBody());
        return result;
    }

    /**
     * 解析响应报文为实体类
     */
    public static AtaResponse parseResponse(byte[] bin) {
        AtaResponse result = new AtaResponse();
        AtaMessage ataMessage = parse(bin);
        String line = ataMessage.firstLine;
        String[] parts = line.split(" ");
        if (parts.length > 0) {
            result.setProtocol(parts[0]);
        }
        if (parts.length > 1) {
            result.setStatusCode(parts[1]);
        }
        if (parts.length > 2) {
            result.setStatusText(parts[2]);
        }
        result.setHeaders(ataMessage.getHeaders());
        result.setBody(ataMessage.getBody());
        return result;
    }

    private static AtaMessage parse(byte[] bin) {
        int headLen = 0;
        for (int i = 0; i < bin.length; i++) {
            byte b = bin[i];
            if (b == '\n' && i + 1 < bin.length) {
                if (bin[i + 1] == '\n') {
                    headLen = i;
                    break;
                }
            }
        }

        byte[] headBytes = new byte[headLen];

        // headLen index为分割符，headLen +1 为body 起始index
        int bodyStartIndex = headLen + 1;
        int bodyLength = bin.length - bodyStartIndex;
        byte[] bodyBytes = new byte[bodyLength];
        System.arraycopy(bin, 0, headBytes, 0, headLen);
        System.arraycopy(bin, bodyStartIndex, bodyBytes, 0, bodyLength);

        AtaMessage result = new AtaMessage();
        result.body = bodyBytes;
        parseHeaders(result, headBytes);
        return result;
    }

    private static void parseHeaders(AtaMessage ataMessage, byte[] headBytes) {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        String headStr = new String(headBytes);
        String[] lines = headStr.split("\n");
        for (int i = lines.length - 1; i >= 0; i--) {
            String line = lines[i];
            if (i == 0) {
                //请求首行处理
                ataMessage.setFirstLine(line);
            } else {
                int kEndIndex = line.indexOf(":");
                if (kEndIndex >= 0) {
                    String key = line.substring(0, kEndIndex);
                    String value = line.substring(kEndIndex + 1);
                    headers.put(key, value);
                } else {
                    throw new IllegalArgumentException("ata headers is error format");
                }
            }
        }
        ataMessage.mHeaders = headers;
    }

}
