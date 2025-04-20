package com.aurxsiu.audiotransmitor;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.InputStream;
import java.net.Socket;

public class MainActivity extends Activity {
    public final static String TAG = "aurxsiuAudioTransmitApp";

    private Button btnToggle;
    private LinearLayout ipContainer;

    private DefaultIpUtil defaultIpUtil = new DefaultIpUtil(this);

    // 控制变量
    public volatile boolean isMonitoring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*Button button = findViewById(R.id.button_start);
        button.setOnClickListener(v -> {
            getConnected();
        });*/
        btnToggle = findViewById(R.id.btnToggle);
        ipContainer = findViewById(R.id.ipContainer);

        btnToggle.setOnClickListener(v -> {
            if (isMonitoring) {
                stopMonitoring();
            } else {
                startMonitoring();
            }
        });
    }

    private void startMonitoring() {
        isMonitoring = true;
        btnToggle.setText("停止检测");
        ipContainer.removeAllViews();

        //todo 开启线程
        defaultIpUtil.detect();
    }

    private void stopMonitoring() {
        isMonitoring = false;
        btnToggle.setText("开始检测");
        ipContainer.removeAllViews();
    }

    public void addIp(String ip) {
        Button ipButton = new Button(this);
        ipButton.setText(ip);
        ipButton.setAllCaps(false); // 保持 IP 小写格式
        ipButton.setOnClickListener(v -> onIpClicked(ip));

        ipContainer.addView(ipButton);
    }

    // 替换成你自己的点击处理逻辑
    private void onIpClicked(String ip) {
        Toast.makeText(this, "点击 IP：" + ip, Toast.LENGTH_SHORT).show();

        //todo 处理
        Log.d(TAG, "onIpClicked: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isMonitoring = false;
    }


    public void getConnected(){
        new Thread(() -> {
            try {
                Socket socket = new Socket("192.168.78.123", 20233);
                InputStream inputStream = socket.getInputStream();

                int sampleRate = 44100;
                int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                int bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

                AudioTrack audioTrack = new AudioTrack.Builder()
                        .setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build())
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(audioFormat)
                                .setSampleRate(sampleRate)
                                .setChannelMask(channelConfig)
                                .build())
                        .setBufferSizeInBytes(bufferSize)
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .build();

                audioTrack.play();

                byte[] buffer = new byte[bufferSize];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    audioTrack.write(buffer, 0, len);
                }

                audioTrack.stop();
                audioTrack.release();
                inputStream.close();
                socket.close();

            } catch (Exception e) {
                Log.e(TAG, "播放异常", e);
            }
        }).start();

    }


}