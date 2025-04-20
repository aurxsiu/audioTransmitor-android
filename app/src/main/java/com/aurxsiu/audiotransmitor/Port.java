package com.aurxsiu.audiotransmitor;

public enum Port {
    windowsDetectListen(25424),
    androidGetDetectedListen(25425),
    audioListen(25426);
    private final int port;

    public int getPort() {
        return port;
    }

    Port(int port) {
        this.port = port;
    }
}

