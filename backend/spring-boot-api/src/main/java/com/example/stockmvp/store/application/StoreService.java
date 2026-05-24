package com.example.stockmvp.store.application;

import com.example.stockmvp.shared.error.DuplicateResourceException;
import com.example.stockmvp.shared.error.ResourceNotFoundException;
import com.example.stockmvp.store.domain.Store;
import com.example.stockmvp.store.infrastructure.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public List<StoreDtos.StoreResponse> listStores() {
        return storeRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(StoreDtos.StoreResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoreDtos.StoreResponse getStore(Long id) {
        return StoreDtos.StoreResponse.from(findStore(id));
    }

    @Transactional
    public StoreDtos.StoreResponse createStore(StoreDtos.CreateStoreRequest request) {
        if (storeRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Store code already exists");
        }
        Store store = storeRepository.save(new Store(request.code(), request.name()));
        return StoreDtos.StoreResponse.from(store);
    }

    @Transactional
    public StoreDtos.StoreResponse updateStore(Long id, StoreDtos.UpdateStoreRequest request) {
        Store store = findStore(id);
        storeRepository.findByCode(request.code())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Store code already exists");
                });

        store.update(request.code(), request.name(), request.active());
        return StoreDtos.StoreResponse.from(store);
    }

    @Transactional
    public void deactivateStore(Long id) {
        findStore(id).deactivate();
    }

    public Store findActiveStore(Long id) {
        Store store = findStore(id);
        if (!store.isActive()) {
            throw new ResourceNotFoundException("Store not found");
        }
        return store;
    }

    private Store findStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
    }
}
