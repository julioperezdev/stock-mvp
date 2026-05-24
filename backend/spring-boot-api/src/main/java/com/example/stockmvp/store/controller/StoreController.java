package com.example.stockmvp.store.controller;

import com.example.stockmvp.store.application.StoreDtos;
import com.example.stockmvp.store.application.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<StoreDtos.StoreResponse> listStores() {
        return storeService.listStores();
    }

    @GetMapping("/{id}")
    public StoreDtos.StoreResponse getStore(@PathVariable Long id) {
        return storeService.getStore(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreDtos.StoreResponse createStore(@Valid @RequestBody StoreDtos.CreateStoreRequest request) {
        return storeService.createStore(request);
    }

    @PutMapping("/{id}")
    public StoreDtos.StoreResponse updateStore(@PathVariable Long id, @Valid @RequestBody StoreDtos.UpdateStoreRequest request) {
        return storeService.updateStore(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStore(@PathVariable Long id) {
        storeService.deactivateStore(id);
    }
}
