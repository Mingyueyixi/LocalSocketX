package com.lu.ata;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lu.ata.client.R;

/**
 * @author Lu
 * @date 2024/12/15 16:11
 * @description
 */
public class MainClientActivity extends Activity {
    private EditText mEtSendBox;
    private TextView mTvResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_client_activity);
        mEtSendBox = findViewById(R.id.et_send_box);
        mTvResponse = findViewById(R.id.tv_response);
        findViewById(R.id.btn_send).setOnClickListener(v -> {
            String text = mEtSendBox.getText().toString();
            AtaClient client = new AtaClient("ata");
            new AtaCall.Builder(client)
                    .setUrl("/test")
                    .method("POST")
                    .body(text.getBytes())
                    .execute(new OnResponseListener() {
                        @Override
                        public void onSuccess(AtaResponse ataResponse) {
                            Toast.makeText(getBaseContext(), ataResponse.getBodyString(), Toast.LENGTH_SHORT).show();
                            mTvResponse.setText(ataResponse.toString());
                        }

                        @Override
                        public void onFailure(Throwable error) {
                            Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }
}
