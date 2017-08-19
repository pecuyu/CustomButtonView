package com.pecuyu.custombuttonview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    CustomButtonView buttonView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonView = (CustomButtonView) findViewById(R.id.id_btn_view);
//        buttonView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        buttonView.setOnStateChangeListener(new CustomButtonView.OnStateChangeListener() {
            @Override
            public void onStart() {
                Log.e("TAG", "onStart");
                buttonView.setBtnText("点击结束");
                buttonView.setMsgText("正在录屏...");
            }

            @Override
            public void onStop() {
                Log.e("TAG", "onStop");
                buttonView.setBtnText("点击开始");
                buttonView.setMsgText("上次录屏时长...");
            }
        });

    }
}
