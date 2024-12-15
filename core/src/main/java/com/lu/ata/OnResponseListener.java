package com.lu.ata;

/**
 * @author Lu
 * @date 2024/12/9 0:01
 * @description
 */
public interface OnResponseListener {

    void onSuccess(AtaResponse ataResponse);
    void onFailure(Throwable error);
}
