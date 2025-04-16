package com.aurxsiu.audiotransmitor;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.InputStream;
import java.net.Socket;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button_start);
        button.setOnClickListener(v -> {
            getConnected();
        });
    }

    public void getConnected(){
        new Thread(() -> {
            try {
                Socket socket = new Socket("192.168.78.123", 20232);
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
                Log.e("AudioStream", "播放异常", e);
            }
        }).start();

    }
}