package com.lu.ata;

import android.accounts.NetworkErrorException;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author Lu
 * @date 2024/12/8 23:24
 * @description
 */
public class AtaClient implements Closeable {
    private final LocalSocket mLocalSocket;
    private final String address;
    private final AtaConnection mClientConnection;

    public AtaClient(String address) {
        this.address = address;
        this.mLocalSocket = new LocalSocket();
        this.mClientConnection = new AtaConnection(mLocalSocket);
    }

    public void connect() throws IOException {
        if (!mLocalSocket.isConnected()) {
            mLocalSocket.connect(new LocalSocketAddress(address));
            mClientConnection.prepare();
        }

    }

    public AtaResponse request(AtaRequest request) throws IOException, NetworkErrorException {
        connect();
        boolean send = mClientConnection.sendBytes(request.toBytes());
        if (!send) {
            throw new NetworkErrorException("send request failed");
        }
        return AtaMessageParser.parseResponse(mClientConnection.readBytes());

    }

    public void close() throws IOException {
        mClientConnection.close();
    }

    public AtaCall.Builder newCall(String url) {
        return new AtaCall.Builder(this).setUrl(url);
    }
}
