package com.hospital.hospitalmanagement.blockchain;

public class Block {
    public String hash;
    public String previousHash;
    private String data; // This will store patient data hash
    private long timestamp;

    // Constructor
    public Block(String data, String previousHash) {
        this.data = data;
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    // ✅ This method is needed for Thymeleaf to access "timestamp"
    public long getTimestamp() {
        return timestamp;
    }

    public String calculateHash() {
        String input = previousHash + Long.toString(timestamp) + data;
        return StringUtil.applySha256(input);
    }

    public String getData() {
        return data;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }
}
