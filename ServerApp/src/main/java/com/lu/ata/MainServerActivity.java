package com.lu.ata;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.lu.ata.server.R;

import java.text.SimpleDateFormat;


/**
 * @author Lu
 * @date 2024/12/15 15:57
 * @description
 */
public class MainServerActivity extends Activity implements ServerletUtil.Listener {
    private TextView textContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_server_activity);
        textContent = findViewById(R.id.tv_server_text);
        ServerletUtil.getInstance().addListener(this);
    }

    @Override
    public void onAtaClientRequest(AtaRequest request, AtaResponse response) {
        String dateText = new SimpleDateFormat().format(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer()
                .append(dateText + "\n")
                .append("request: \n")
                .append(request.toString())
                .append("============================================\n")
                .append("response:\n")
                .append(response.toString());
        textContent.setText(sb);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerletUtil.getInstance().removeListener(this);
    }
}
