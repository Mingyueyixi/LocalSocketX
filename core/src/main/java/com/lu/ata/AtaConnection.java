package com.lu.ata;

import android.net.LocalSocket;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Lu
 * @date 2024/12/8 22:22
 * @description
 */
public class AtaConnection implements Closeable {
    private LocalSocket mLocalSocket;
    private DataInputStream input;
    private DataOutputStream output;

    public AtaConnection(LocalSocket localSocket) {
        mLocalSocket = localSocket;
    }

    void prepare() throws IOException {
        try {
            input = new DataInputStream(mLocalSocket.getInputStream());
            output = new DataOutputStream(mLocalSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取一次绘话消息的内容
     *
     * @return
     */
    public byte[] readBytes() {
        if (input == null) {
            return null;
        }
        //消息长度
        try {
            int length = input.readInt();
            byte[] buffer = new byte[length];
            input.readFully(buffer);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送一次消息
     *
     * @param data
     * @return
     */
    public boolean sendBytes(byte[] data) {
        if (output == null) {
            return false;
        }
        if (data == null || data.length == 0) {
            return false;
        }
        try {
            output.writeInt(data.length);
            output.write(data);
            output.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        if (input != null) {
            input.close();
            output = null;
        }
        if (output != null) {
            output.close();
            output = null;
        }
        if (mLocalSocket != null) {
            mLocalSocket.close();
        }
    }


    public boolean isClosed() {
        return mLocalSocket.isClosed();
    }
}
