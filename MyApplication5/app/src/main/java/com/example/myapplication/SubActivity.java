package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class SubActivity extends AppCompatActivity {
    private Button mHomeBtn, mStartBtn, mPauseBtn;
    private TextView mTimeTextView;
    private Thread timeThread = null;
    private Boolean isRunning = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);


        mHomeBtn = (Button) findViewById(R.id.btn_Home);
        mStartBtn = (Button) findViewById(R.id.btn_start);
        mPauseBtn = (Button) findViewById(R.id.btn_pause);
        mTimeTextView = (TextView) findViewById(R.id.timeView);

        mHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, MainActivity.class);
                startActivity(intent); //액티비티 이동
            }
        });

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
             //   v.setVisibility(View.GONE);
             //   mPauseBtn.setVisibility(View.VISIBLE);


                timeThread = new Thread(new timeThread());
                timeThread.start();
            }
        });
        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = !isRunning;
                if (isRunning) {
                    mPauseBtn.setText("일시정지");
                } else {
                    mPauseBtn.setText("재시작");
                }
            }
        });
    }
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int mSec = msg.arg1 % 100;
                int sec = (msg.arg1 / 100) % 60;
                int min = (msg.arg1 / 100) / 60;
                //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
                @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d", min, sec, mSec);
                mTimeTextView.setText(result);


            }
        };

        public class timeThread implements Runnable {
            @Override
            public void run() {
                int i = 0;

                while (true) {
                    while (isRunning) { //일시정지를 누르면 멈춤
                        Message msg = new Message();
                        msg.arg1 = i++;
                        handler.sendMessage(msg);

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTimeTextView.setText("");
                                    mTimeTextView.setText("00:00:00");
                                }
                            });
                            return; // 인터럽트 받을 경우 return
                        }
                    }
                }
            }
        }




    }