package com.healthchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthchain.ai.RiskScorer;
import com.healthchain.crypto.CryptoUtils;
import com.healthchain.identity.Identity;
import com.healthchain.ledger.Block;
import com.healthchain.ledger.Blockchain;
import com.healthchain.ledger.Transaction;
import com.healthchain.model.HealthRecord;

import java.security.PublicKey;
import java.util.*;

import static spark.Spark.*;

public class ApiServer {
    private final ObjectMapper mapper = new ObjectMapper();
    private final RiskScorer scorer = new RiskScorer();
    private final Blockchain blockchain = new Blockchain(3);

    private final Map<String, Identity> identities = new HashMap<>();
    private final Map<String, String> encryptedRecords = new HashMap<>();
    private final Map<String, byte[]> aesKeys = new HashMap<>();
    private final Map<String, byte[]> aesIvs = new HashMap<>();

    public void start(int port) {
        port(port);

        post("/identity/:id", (req, res) -> {
            String id = req.params(":id");
            Identity identity = new Identity(id);
            identities.put(id, identity);
            blockchain.registerIdentity(id, identity.getPublicKey());
            res.type("application/json");
            return mapper.writeValueAsString(Map.of(
                    "id", id,
                    "publicKey", identity.getPublicKeyBase64()
            ));
        });

        post("/record", (req, res) -> {
            HealthRecord record = mapper.readValue(req.body(), HealthRecord.class);
            String ownerId = record.providerId;
            Identity owner = identities.get(ownerId);
            if (owner == null) {
                res.status(400);
                return "Unknown provider identity";
            }

            double risk = scorer.score(record);
            String recordJson = mapper.writeValueAsString(record);

            byte[] key = CryptoUtils.generateAesKey(128).getEncoded();
            byte[] iv = UUID.randomUUID().toString().substring(0,12).getBytes();
            String ciphertextB64 = CryptoUtils.encryptAesGcm(recordJson, key, iv);

            String recordId = record.recordId != null ? record.recordId : UUID.randomUUID().toString();
            encryptedRecords.put(recordId, ciphertextB64);
            aesKeys.put(recordId, key);
            aesIvs.put(recordId, iv);

            String dataHash = CryptoUtils.sha256(ciphertextB64);
            Transaction tx = new Transaction(recordId, dataHash, ownerId, record.patientId, System.currentTimeMillis());
            tx.sign(owner.getPrivateKey());
            boolean ok = blockchain.submitBlock(List.of(tx));
            if (!ok) {
                res.status(500);
                return "Failed to submit block";
            }

            res.type("application/json");
            return mapper.writeValueAsString(Map.of(
                    "recordId", recordId,
                    "riskScore", risk,
                    "ciphertextB64", ciphertextB64
            ));
        });

        get("/record/:recordId", (req, res) -> {
            String recordId = req.params(":recordId");
            String ct = encryptedRecords.get(recordId);
            if (ct == null) { res.status(404); return "Not found"; }
            res.type("application/json");
            return mapper.writeValueAsString(Map.of("recordId", recordId, "ciphertextB64", ct));
        });

        get("/record/:recordId/decrypt", (req, res) -> {
            String recordId = req.params(":recordId");
            String ct = encryptedRecords.get(recordId);
            byte[] key = aesKeys.get(recordId);
            byte[] iv = aesIvs.get(recordId);
            if (ct == null || key == null || iv == null) { res.status(404); return "Not found"; }
            String pt = CryptoUtils.decryptAesGcm(ct, key, iv);
            res.type("application/json");
            return pt;
        });

        get("/chain", (req, res) -> {
            List<Block> blocks = blockchain.getBlocks();
            res.type("application/json");
            return mapper.writeValueAsString(blocks);
        });

        get("/health", (req, res) -> "OK");

        init();
        awaitInitialization();
    }
}

