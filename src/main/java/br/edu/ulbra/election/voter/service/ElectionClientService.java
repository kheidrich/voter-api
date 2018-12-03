package br.edu.ulbra.election.voter.service;

import br.edu.ulbra.election.voter.client.ElectionClient;
import br.edu.ulbra.election.voter.output.v1.ElectionOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElectionClientService {
    private final ElectionClient electionClient;

    @Autowired
    public ElectionClientService(ElectionClient electionClient){
        this.electionClient = electionClient;
    }

    public List<ElectionOutput> getVoterId(Long voterId){
        return this.electionClient.getByVoterId(voterId);
    }
}
