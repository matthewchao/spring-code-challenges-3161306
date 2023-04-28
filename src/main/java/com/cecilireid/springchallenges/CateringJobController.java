package com.cecilireid.springchallenges;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("cateringJobs")
public class CateringJobController {
    private static final String IMAGE_API = "https://foodish-api.herokuapp.com";
    private final CateringJobRepository cateringJobRepository;
    WebClient client;

    public CateringJobController(CateringJobRepository cateringJobRepository, WebClient.Builder webClientBuilder) {
        this.cateringJobRepository = cateringJobRepository;
    }

    @GetMapping
    public List<CateringJob> getCateringJobs() {
        return cateringJobRepository.findAll();
    }

    @GetMapping("/{id}")
    public CateringJob getCateringJobById(@PathVariable Long id) {
        if (cateringJobRepository.existsById(id)) {
            return cateringJobRepository.findById(id).get();
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findByStatus")
    public List<CateringJob> getCateringJobsByStatus(@RequestParam Status status) {
        return cateringJobRepository.findByStatus(status);
    }

    @PostMapping("/create")
    public CateringJob createCateringJob(@RequestBody CateringJob job) {
        return cateringJobRepository.save(job);
    }

    @PutMapping("/{id}")
    public CateringJob updateCateringJob(@RequestBody CateringJob cateringJob, @PathVariable Long id) {
        if (cateringJobRepository.existsById(id)) {
            cateringJob.setId(id);
            return cateringJobRepository.save(cateringJob);
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    @SneakyThrows
    @PatchMapping("/{id}")
    public CateringJob patchCateringJob(@PathVariable long id, @RequestBody JsonNode json) {
        if (cateringJobRepository.existsById(id)) {
            CateringJob cateringJob = cateringJobRepository.findById(id).get();
            ObjectMapper om = new ObjectMapper();
            ObjectReader or = om.readerForUpdating(cateringJob);
            or.readValue(json);
            return cateringJobRepository.save(cateringJob);
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    public Mono<String> getSurpriseImage() {
        return null;
    }
}
