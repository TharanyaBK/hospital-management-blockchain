package com.healthchain.ledger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthchain.crypto.CryptoUtils;

import java.util.ArrayList;
import java.util.List;

public class Block {
    private int index;
    private long timestampMs;
    private List<Transaction> transactions = new ArrayList<>();
    private String previousHash;
    private long nonce;
    private String hash;

    public Block() {}

    public Block(int index, long timestampMs, List<Transaction> transactions, String previousHash) {
        this.index = index;
        this.timestampMs = timestampMs;
        this.transactions = transactions;
        this.previousHash = previousHash;
    }

    public int getIndex() { return index; }
    public long getTimestampMs() { return timestampMs; }
    public List<Transaction> getTransactions() { return transactions; }
    public String getPreviousHash() { return previousHash; }
    public long getNonce() { return nonce; }
    public String getHash() { return hash; }

    public String calculateHash() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String txJson = mapper.writeValueAsString(transactions);
            String input = index + ":" + timestampMs + ":" + txJson + ":" + previousHash + ":" + nonce;
            return CryptoUtils.sha256(input);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize transactions", e);
        }
    }

    public void mine(int difficulty) {
        String prefix = "0".repeat(Math.max(0, difficulty));
        while (true) {
            this.hash = calculateHash();
            if (hash.startsWith(prefix)) {
                break;
            }
            nonce++;
        }
    }
}

