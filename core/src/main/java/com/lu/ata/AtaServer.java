package com.lu.ata;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.CountDownTimer;
import android.os.HandlerThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lu
 * @date 2024/12/8 16:00
 * @description 服务端启动
 */
public class AtaServer {
    private LocalServerSocket mServerSocket;
    private ReceiveThread mReceiveThread;
    private OnListener mOnListener;
    private HandlerThread mDaemonThread;
    private CountDownTimer mDaemonTimer;
    private List<AtaConnection> mConnections = new ArrayList<>();

    public void startServer(String address) {
        try {
            if (mDaemonThread == null || !mDaemonThread.isAlive()) {
                mDaemonThread = new HandlerThread("ata-server-daemon") {
                    @Override
                    protected void onLooperPrepared() {
                        super.onLooperPrepared();
                        mDaemonTimer = new CountDownTimer(Long.MAX_VALUE, 1000L) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                Iterator<AtaConnection> it = mConnections.iterator();
                                while (it.hasNext()) {
                                    AtaConnection ele = it.next();
                                    if (ele == null) {
                                        it.remove();
                                        continue;
                                    }
                                    if (ele.isClosed()) {
                                        it.remove();
                                        dispatchCloseSession(ele);
                                    }
                                }
                            }

                            @Override
                            public void onFinish() {

                            }
                        };
                    }
                };
                mDaemonThread.start();
            }
            mServerSocket = new LocalServerSocket(address);
            if (mReceiveThread == null || !mReceiveThread.isAlive()) {
                mReceiveThread = new ReceiveThread();
                mReceiveThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatchCloseSession(AtaConnection ele) {
        if (mOnListener != null) {
            mOnListener.onCloseSession(ele);
        }
    }


    public void setOnListener(OnListener listener) {
        this.mOnListener = listener;
    }

    public void stopServer() {
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mOnListener != null) {
            mOnListener.onStopServer();
        }
        if (mDaemonTimer != null) {
            mDaemonTimer.onFinish();
        }
        if (mDaemonThread != null && mDaemonThread.isAlive()) {
            mDaemonThread.quitSafely();
            mDaemonThread = null;
        }
        mOnListener = null;
    }

    public interface OnListener {

        /**
         * 收到请求
         */
        AtaResponse onRequest(AtaRequest request);

        /**
         * 出错
         */
        void onError(AtaConnection session, Throwable error);

        /**
         * 关闭绘话
         */
        void onCloseSession(AtaConnection session);

        /**
         * 关闭
         */
        void onStopServer();
    }

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            while (true) {
                LocalSocket client;
                try {
                    client = mServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    dispatchError(null, e);
                    stopServer();
                    break;
                }
                AtaConnection connection = null;

                connection = new AtaConnection(client);
                try {
                    connection.prepare();
                    mConnections.add(connection);
                    handleClient(connection);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void dispatchError(AtaConnection session, IOException e) {
        if (mOnListener != null) {
            mOnListener.onError(session, e);
        }
    }

    private void handleClient(AtaConnection conn) {
        try {
            byte[] msgBin = conn.readBytes();
            AtaRequest request = AtaMessageParser.parseRequest(msgBin);

            if (mOnListener != null) {
                AtaResponse reply = mOnListener.onRequest(request);
                if (reply != null) {
                    conn.sendBytes(reply.toBytes());
                }
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
