package com.cyber.difenda.controller;

import com.cyber.difenda.model.Domain;
import com.cyber.difenda.repository.DomainRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/domain")
public class DomainController extends BaseController {

    private final DomainRepository domainRepository;

    public DomainController(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    @GetMapping
    public List<Domain> getAllDomains() {
        return domainRepository.findAll();
    }

    @PostMapping
    public Domain createDomain(@RequestBody Domain domain) {
        return domainRepository.save(domain);
    }
}
