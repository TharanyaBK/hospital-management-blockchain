package com.hospital.hospitalmanagement.controller;

import com.hospital.hospitalmanagement.blockchain.Block;
import com.hospital.hospitalmanagement.blockchain.Blockchain;
import com.hospital.hospitalmanagement.blockchain.StringUtil;
import com.hospital.hospitalmanagement.model.Patient;
import com.hospital.hospitalmanagement.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    // Show all patients
    @GetMapping("/patients/view")
    public String viewAllPatients(Model model) {
        List<Patient> patients = patientRepository.findAll();
        model.addAttribute("patients", patients);
        return "patients";
    }

    // Show add patient form
    @GetMapping("/patients/add")
    public String showAddPatientForm(Model model) {
        Patient patient = new Patient();
        model.addAttribute("patient", patient);
        return "add_patient";
    }

    // Save new patient and add to blockchain ✅
    @PostMapping("/patients/save")
    public String savePatient(@ModelAttribute("patient") Patient patient) {
        // Save patient to DB
        patientRepository.save(patient);

        // Add patient to blockchain ✅
        String patientData = patient.getName() + patient.getAge() + patient.getGender() + patient.getDisease();
        String patientHash = StringUtil.applySha256(patientData);
        String previousHash = Blockchain.chain.size() > 0 ? Blockchain.getLatestBlock().hash : "0";
        Block newBlock = new Block(patientHash, previousHash);
        Blockchain.addBlock(newBlock);

        System.out.println("✅ Added patient to blockchain with hash: " + newBlock.hash);

        return "redirect:/patients/view"; // Redirect back to patients list
    }

    // View Blockchain as HTML page
    @GetMapping("/blockchain")
    public String viewBlockchain(Model model) {
        model.addAttribute("blockchain", Blockchain.chain);
        return "blockchain";
    }

    // ✅ View Blockchain as JSON
    @GetMapping("/blockchain/json")
    @ResponseBody
    public List<Block> getBlockchain() {
        return Blockchain.chain;
    }

    // ✅ View all patients as JSON (for API)
    @GetMapping("/api/patients")
    @ResponseBody
    public List<Patient> getAllPatientsJson() {
        return patientRepository.findAll();
    }
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("patients", patientRepository.findAll());
        model.addAttribute("patient", new Patient());
        model.addAttribute("blockchain", Blockchain.chain);
        return "dashboard";
    }

}
