package com.healthchain.ledger;

import com.healthchain.crypto.CryptoUtils;

import java.security.PublicKey;
import java.util.*;

public class Blockchain {
    private final List<Block> chain = new ArrayList<>();
    private final int difficulty;
    private final Map<String, PublicKey> identityRegistry = new HashMap<>();

    public Blockchain(int difficulty) {
        this.difficulty = difficulty;
        chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        Transaction genesisTx = new Transaction("genesis", CryptoUtils.sha256("genesis"), "network", "network", System.currentTimeMillis());
        List<Transaction> txs = new ArrayList<>();
        txs.add(genesisTx);
        Block block = new Block(0, System.currentTimeMillis(), txs, "0");
        block.mine(Math.max(1, difficulty));
        return block;
    }

    public synchronized void registerIdentity(String id, PublicKey publicKey) {
        identityRegistry.put(id, publicKey);
    }

    public synchronized boolean submitBlock(List<Transaction> transactions) {
        int index = chain.size();
        String prevHash = chain.get(chain.size() - 1).getHash();

        // verify signatures and uniqueness
        for (Transaction tx : transactions) {
            PublicKey pk = identityRegistry.get(tx.getOwnerId());
            if (pk == null || !tx.verify(pk)) {
                return false;
            }
        }

        Block block = new Block(index, System.currentTimeMillis(), transactions, prevHash);
        block.mine(difficulty);
        chain.add(block);
        return true;
    }

    public synchronized boolean isValid() {
        String prefix = "0".repeat(Math.max(0, difficulty));
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.getHash().startsWith(prefix)) return false;
            if (!current.getHash().equals(current.calculateHash())) return false;
            if (!current.getPreviousHash().equals(previous.getHash())) return false;
            for (Transaction tx : current.getTransactions()) {
                PublicKey pk = identityRegistry.get(tx.getOwnerId());
                if (pk == null || !tx.verify(pk)) return false;
            }
        }
        return true;
    }

    public synchronized List<Block> getBlocks() {
        return Collections.unmodifiableList(chain);
    }
}

