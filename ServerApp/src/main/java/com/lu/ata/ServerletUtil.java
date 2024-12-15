package com.lu.ata;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Lu
 * @date 2024/12/15 16:02
 * @description
 */
public class ServerletUtil {
    private List<Listener> listeners = new CopyOnWriteArrayList<>();
    private static ServerletUtil sInstance;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ServerletUtil() {
    }
    public static ServerletUtil getInstance() {
        if (sInstance == null) {
            sInstance = new ServerletUtil();
        }
        return sInstance;
    }
    public void addListener(Listener listener) {
        if (listeners == null) {
            return;
        }
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    public void dispatchOnRequest(AtaRequest request, AtaResponse resp) {
        runOnUi(() -> {
            for (Listener listener : listeners) {
                listener.onAtaClientRequest(request, resp);
            }
        });
    }

    private void runOnUi(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        }
        else {
            mHandler.post(runnable);
        }

    }

    public interface Listener {
        void onAtaClientRequest(AtaRequest request, AtaResponse response);
    }
}
