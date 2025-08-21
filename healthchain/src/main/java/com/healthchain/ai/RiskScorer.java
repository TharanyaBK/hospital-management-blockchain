package com.healthchain.ai;

import com.healthchain.model.HealthRecord;

public class RiskScorer {
    public double score(HealthRecord record) {
        double base = 0.2;
        if (record.encounterType != null) {
            if (record.encounterType.toLowerCase().contains("icu")) base += 0.5;
            if (record.encounterType.toLowerCase().contains("emergency")) base += 0.3;
        }
        if (record.notes != null) {
            String n = record.notes.toLowerCase();
            if (n.contains("sepsis")) base += 0.4;
            if (n.contains("chest pain")) base += 0.3;
            if (n.contains("fall")) base += 0.1;
        }
        return Math.min(1.0, base);
    }
}

