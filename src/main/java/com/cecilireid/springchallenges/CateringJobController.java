package com.cecilireid.springchallenges;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("cateringJobs")
public class CateringJobController {
//    private static final String IMAGE_API = "https://foodish-api.herokuapp.com"; // this was not live as of 2023-04-30
//    private static final String IMAGE_API = "https://cdn.shibe.online"; // commented out code shows getting a particular image
    private static final String IMAGE_API = "https://shibe.online/api/shibes";
    private final CateringJobRepository cateringJobRepository;
    WebClient client;

    public CateringJobController(CateringJobRepository cateringJobRepository, WebClient.Builder webClientBuilder) {
        this.cateringJobRepository = cateringJobRepository;
        this.client = webClientBuilder.baseUrl(IMAGE_API).build();
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
    @Transactional
    @PatchMapping("/{id}")
    public CateringJob patchCateringJob(@PathVariable long id, @RequestBody JsonNode json) {
        if (cateringJobRepository.existsById(id)) {
            CateringJob cateringJob = cateringJobRepository.findById(id).get();
            ObjectMapper om = new ObjectMapper();
            ObjectReader or = om.readerForUpdating(cateringJob);
            or.readValue(json); // this updates the cateringJob object
            return cateringJob;
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private String handleNotFound() {
        return "Job not found\n";
    }

//    @GetMapping(value="/surpriseImage",
//            produces = MediaType.IMAGE_JPEG_VALUE
//    )
//    public Mono<byte[]> getSurpriseImage() {
//        return client.get()
//                .uri("/shibes/6a6903f0fc4e4f820d04751e3f4ba260e190ead6.jpg")
//                .retrieve()
//                .bodyToMono(byte[].class);
//    }
    @GetMapping(value="/surpriseImage")
    public Mono<String> getSurpriseImage() {
        return client.get()
                .retrieve()
                .bodyToMono(String[].class)
                .map(strings -> strings[0]);
    }
}
