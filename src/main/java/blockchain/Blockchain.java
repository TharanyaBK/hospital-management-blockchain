package com.hospital.hospitalmanagement.blockchain;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    public static List<Block> chain = new ArrayList<>();

    // ✅ Static block: creates the Genesis Block once
    static {
        Block genesisBlock = new Block("0", "0");
        chain.add(genesisBlock);
    }

    // ✅ Add a new block to the chain
    public static void addBlock(Block newBlock) {
        chain.add(newBlock);
    }

    // ✅ Get the latest block
    public static Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    // ✅ Get the full chain (used for displaying)
    public static List<Block> getChain() {
        return chain;
    }

    // ✅ Validate the chain
    public static boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                return false;
            }

            if (!currentBlock.previousHash.equals(previousBlock.hash)) {
                return false;
            }
        }
        return true;
    }
}
