package com.aurxsiu.audiotransmitor;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class DefaultIpUtil {
    public final static String TAG = MainActivity.TAG+":"+DefaultIpUtil.class.getName();
    private final MainActivity activity;

    public DefaultIpUtil(MainActivity activity) {
        this.activity = activity;
    }

    public void getDevice(){

    }
    //todo 先做连接单一设备的功能,多个设备以后再说,我也没设备来测试
    public final String IpLogFileName = "DeviceIpLog.log";
    public String getDefaultIpFromFile() throws Exception{
        File file = new File(activity.getFilesDir(), IpLogFileName);
        Log.d(TAG, "getDefaultIp: filePath:"+file.getPath());
        if(file.isFile()){
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                String s = new String(fileInputStream.readAllBytes(), StandardCharsets.UTF_8);
                if(!s.isEmpty()){
                    return s;
                }
            }
        }

        return null;
    }

    public void setDefaultIpToFile(String ip) throws Exception{
        File file = new File(activity.getFilesDir(), IpLogFileName);
        file.createNewFile();
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(ip.getBytes());
        }
    }

    private boolean haveDetected = false;

    public boolean isHaveDetected(){
        synchronized (DefaultIpUtil.class){
            return haveDetected;
        }
    }
    public void setHaveDetected(boolean set){
        synchronized (DefaultIpUtil.class){
            haveDetected = set;
        }
    }
    public static final String discoveryMessage = MainActivity.TAG+":ToGetDeviceAvailable";
    public static final String WindowsRequireConnectMessage = MainActivity.TAG+":requireConnect";
    public void detect(){

        new Thread(()->{
            try (DatagramSocket socket = new DatagramSocket(null)) {
                WifiManager wifi = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiManager.MulticastLock multicastLock = wifi.createMulticastLock("udp_lock");
                multicastLock.setReferenceCounted(true);
                multicastLock.acquire();
                Log.d(TAG, "Multicast lock acquired");

                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(Port.androidGetDetectedListen.getPort()));


                byte[] buffer = new byte[1024];

                while (activity.isMonitoring) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    Log.d(TAG, "Socket is bound: " + socket.isBound());
                    Log.d(TAG, "Socket is closed: " + socket.isClosed());
                    Log.d(TAG, "detect: " + socket.getLocalPort());

                    socket.receive(packet);
                    String message = new String(
                            packet.getData(),
                            0,
                            packet.getLength()
                    );

                    // 如果收到的是你的应用广播消息
                    if (message.contains(WindowsRequireConnectMessage)) {
                        Log.d(TAG, "detect: getRequire");
                        // 在此可以返回确认消息给服务器
                        String response =
                                "Application found: " +
                                        InetAddress.getLocalHost().getHostAddress();
                        DatagramPacket responsePacket = new DatagramPacket(
                                response.getBytes(),
                                response.length(),
                                packet.getAddress(),
                                packet.getPort()
                        );
                        socket.send(responsePacket);
                        activity.addIp(packet.getAddress().toString());
                    }
                }
                multicastLock.release();
            }catch (Exception e){
                Log.d(TAG, "detect: false");
                throw new RuntimeException(e);
            }
        }).start();

        /*new Thread(() -> {
            try {
                WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                int ip = dhcpInfo.gateway; // 热点 IP
                String hostIp = String.format("%d.%d.%d.%d",
                        (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
                Log.d("HotspotIP", "本机热点IP: " + hostIp);


                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);

                InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
                String message = "aurxsiuAudioTransmitApp:requireConnect";
                DatagramPacket packet = new DatagramPacket(
                        message.getBytes(),
                        message.length(),
                        broadcastAddress,
                        50000 // 目标端口，和 PC 端一致
                );

                while (true) {
                    socket.send(packet);
                    Log.d("UdpSender", "广播已发送");
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();*/
    }

    public void detect2(){
        new Thread(()-> {
            try (MulticastSocket multicastSocket = new MulticastSocket(Port.androidGetDetectedListen.getPort())) {
                multicastSocket.joinGroup(InetAddress.getByName("224.0.0.1"));
                while (activity.isMonitoring) {
                    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                    multicastSocket.receive(packet);
                    byte[] data = packet.getData();
                    Log.d(TAG, "detect: " + new String(data));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).start();
    }

}
