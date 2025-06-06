package service;

import java.util.List;

import models.Threat;
import repository.ThreatRepository;

public class ThreatService {

    private ThreatRepository threatRepository;

    public ThreatService(ThreatRepository threatRepository) {
        this.threatRepository = threatRepository;
    }

    // Add new threat
    public boolean addThreat(String title, String description, String severity) {
        Threat threat = new Threat(title, description, severity);
        return threatRepository.addThreat(threat);
    }

    // Get all threats
    public List<Threat> getAllThreats() {
        return threatRepository.getAllThreats();
    }

    // Delete a threat
    public boolean deleteThreat(int id) {
        return threatRepository.deleteThreat(id);
    }
}
