package com.siemens.internship.controller;

import com.siemens.internship.model.Item;
import com.siemens.internship.model.dto.ItemDTO;
import com.siemens.internship.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return new ResponseEntity<>(itemService.findAll(), HttpStatus.OK);
    }

    /*
        I created a DTO for item because I don't want to expose the internal structure (id to be more precise).
        Also, I added validators on the fields of the DTO.
        I switched the status codes.
    */
    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody ItemDTO item, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, List<String>> errors = new HashMap<>();
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.toList());
            errors.put("errors", errorMessages);

            return ResponseEntity.badRequest().body(errors);
        }
        return new ResponseEntity<>(itemService.save(item), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    /*
        I moved the update logic to the service and changed the status code for when the object isn't found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody ItemDTO item) {
        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()){
            existingItem.get().setId(id);
            return new ResponseEntity<>(itemService.updateItem(existingItem.get(), item), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /*
        I changed the status code from **CONFLICT** to **ACCEPTED**.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/process")
    public ResponseEntity<List<Item>> processItems() throws ExecutionException, InterruptedException {
        return new ResponseEntity<>(itemService.processItemsAsync().get(), HttpStatus.OK);
    }
}
