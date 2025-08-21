package com.healthchain;

public class App {
    public static void main(String[] args) {
        ApiServer server = new ApiServer();
        server.start(8080);
    }
}

