package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @SneakyThrows
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        log.info("Start of isInStock");
        //Thread.sleep(10000);
        log.info("end of isInStock");
        List<Inventory> inventoryList = inventoryRepository.findBySkuCodeIn(skuCode);
        return inventoryList.stream().map(inventory -> new InventoryResponse().builder()
                .skuCode(inventory.getSkuCode())
                .isInStock(inventory.getQuantity() > 0)
                .build()
        ).collect(Collectors.toList());
    }
}
