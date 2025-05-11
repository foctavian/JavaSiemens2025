package com.siemens.internship.service;

import com.siemens.internship.model.Item;
import com.siemens.internship.model.dto.ItemDTO;
import com.siemens.internship.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    ConcurrentLinkedQueue<Item> processedItems = new ConcurrentLinkedQueue<>();
    private AtomicInteger processedCount = new AtomicInteger(0);


    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(ItemDTO itemDTO) {
        return itemRepository.save(Item.builder()
                .status(itemDTO.getStatus())
                .email(itemDTO.getEmail())
                .name(itemDTO.getName())
                .description(itemDTO.getDescription()).build());
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

    public Item updateItem(Item existingItem, ItemDTO item) {
        existingItem.setStatus(item.getStatus());
        existingItem.setEmail(item.getEmail());
        existingItem.setName(item.getName());
        existingItem.setDescription(item.getDescription());
        return itemRepository.save(existingItem);
    }

    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     *
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */


    /*
        Changes:
            - Unsafe shared state (processedItems and processedCount) => changed them to thread safe alternatives
             (AtomicInteger, ConcurrentLinkedQueue)
            - Incorrect use of @Async => changed the return type to a Future implementation
            - The method returned immediately without waiting for async operations to complete
            - Poor error handling => integrated the catch of InterruptedException in order to log the interrupt.

            **INSIDE THE CONTROLLER**
            - I added the '.get()' after the method call to wait for the results.
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {

        List<Long> itemIds = itemRepository.findAllIds();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Long id : itemIds) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(100);

                    Item item = itemRepository.findById(id).orElse(null);
                    if (item == null) {
                        return;
                    }
                    item.setStatus("PROCESSED");
                    itemRepository.save(item);
                    processedItems.add(item);
                    processedCount.incrementAndGet();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Error: " + e.getMessage());
                }
            }, executor);
            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> new ArrayList<>(processedItems));
    }


}

