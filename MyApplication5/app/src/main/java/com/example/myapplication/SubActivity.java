package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Queue;


public class SubActivity extends AppCompatActivity {
    private Button mHomeBtn, mStartBtn, mPauseBtn;
    private TextView mTimeTextView;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    static Handler mBluetoothHandler;
    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);


        mHomeBtn = (Button) findViewById(R.id.btn_Home);
        mStartBtn = (Button) findViewById(R.id.btn_start);
        mPauseBtn = (Button) findViewById(R.id.btn_pause);
        mTimeTextView = (TextView) findViewById(R.id.timeView);
        TextView mtvReceiveData = (TextView) findViewById(R.id.tvReceiveData);
        TextView mtvReceiveData2 = (TextView) findViewById(R.id.tvReceiveData2);
        mHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(SubActivity.this, MainActivity.class);
                startActivity(intent); //???????????? ??????
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
                    mPauseBtn.setText("????????????");
                } else {
                    mPauseBtn.setText("?????????");
                }
            }
        });

        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(readMessage.contains("1"))
                    {
                        mtvReceiveData.setText("?????????????????????");
                    }
                    if(readMessage.contains("2"))
                    {
                        mtvReceiveData.setText("???????????????.");
                    }
                    if(readMessage.contains("3"))
                    {
                        mtvReceiveData.setText("???????????????");
                    }
                    if(readMessage.contains("4"))
                    {
                        mtvReceiveData2.setText("????????? ??????");
                    }
                    if(readMessage.contains("5"))
                    {
                        mtvReceiveData2.setText("?????????????????? ????????????");
                    }
                    if(readMessage.contains("6"))
                    {
                        mtvReceiveData2.setText("????????? ?????? ??????");
                    }

                }
                if(msg.what==BT_CONNECTING_STATUS){
                    mtvReceiveData.setText((String) msg.obj);
                }
            }
        };
    }

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int mSec = msg.arg1 % 100;
                int sec = (msg.arg1 / 100) % 60;
                int min = (msg.arg1 / 100) / 60;
                //1000??? 1??? 1000*60 ??? 1??? 1000*60*10??? 10??? 1000*60*60??? ?????????
                @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d", min, sec, mSec);
                mTimeTextView.setText(result);


            }
        };

        public class timeThread implements Runnable {
            @Override
            public void run() {
                int i = 0;

                while (true) {
                    while (isRunning) { //??????????????? ????????? ??????
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
                            return; // ???????????? ?????? ?????? return
                        }
                    }
                }
            }
        }

    public static class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private Queue<byte[]> bufferQueue = new LinkedList<byte[]>();

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
       //         Toast.makeText(getApplicationContext(), "?????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        bytes = mmInStream.read(buffer);
                        bufferQueue.offer(new byte[bytes]);
                        System.arraycopy(buffer, 0, bufferQueue.peek(), 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, bufferQueue.poll()).sendToTarget();
//                      bytes = mmInStream.available();
//                      bytes = mmInStream.read(buffer, 0, bytes);
//                      mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(), "????????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(), "?????? ?????? ??? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
            }
        }
    };
    }