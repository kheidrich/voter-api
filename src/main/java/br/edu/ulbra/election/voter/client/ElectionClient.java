package br.edu.ulbra.election.voter.client;

import br.edu.ulbra.election.voter.output.v1.ElectionOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "election-service", url = "${url.election-service}")
public interface ElectionClient {
    @GetMapping("/v1/voter/{voterId}")
    List<ElectionOutput> getByVoterId(@PathVariable(name = "voterId") Long voterId);
}
