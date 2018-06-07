package com.ningwuyue.sdk.nwyvideoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ningwuyue.sdk.nwyvideoplayersdk.model.VipwebEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.ui.VipWebviewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv_open = findViewById(R.id.tv_open);
        tv_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://m.iqiyi.com/";
                VipwebEntity entity = new VipwebEntity("1", "爱奇艺", url, "爱奇艺", url, "1", "1", "1", "", "");
                VipWebviewActivity.startVipWebViewActivity(MainActivity.this, entity);
            }
        });
    }
}
