package com.hospital.hospitalmanagement;

import com.hospital.hospitalmanagement.blockchain.Block;
import com.hospital.hospitalmanagement.blockchain.Blockchain;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HospitalmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalmanagementApplication.class, args);
	}

	// ✅ Add Genesis block when app starts
	@Bean
	public ApplicationRunner initializer() {
		return args -> {
			if (Blockchain.chain.isEmpty()) {
				Block genesisBlock = new Block("Genesis", "0");
				Blockchain.addBlock(genesisBlock);
				System.out.println("✅ Genesis block added.");
			}
		};
	}
}
