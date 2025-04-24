package com.aurxsiu.audiotransmitor;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.util.Log;

import java.io.InputStream;
import java.net.Socket;

public class Connector {
    public void getConnected(String content){
        new Thread(() -> {
            try {
                Socket socket = new Socket(content, 20233);
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

                Log.d("aurxsiu","bufferSize:"+bufferSize);

                byte[] buffer = new byte[bufferSize];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    //todo 这个值建议后面可以调节
                    if(inputStream.available() > 327680){
                        audioTrack.flush();
                        long skip = inputStream.skip(inputStream.available());
                        Log.d("aurxsiu", "getConnected: flush,skip:"+skip);
                    }else{
                        audioTrack.write(buffer, 0, len);
                    }
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
