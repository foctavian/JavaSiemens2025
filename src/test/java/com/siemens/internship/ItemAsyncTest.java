package com.siemens.internship;

import com.siemens.internship.repository.ItemRepository;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.siemens.internship.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ItemAsyncTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @BeforeEach
    public void setup() {
        itemRepository.deleteAll();

        itemRepository.save(Item.builder()
                .name("Item 1")
                .description("Description 1")
                .status("NEW")
                .email("test1@example.com")
                .build());

        itemRepository.save(Item.builder()
                .name("Item 2")
                .description("Description 2")
                .status("NEW")
                .email("test2@example.com")
                .build());
    }

    @Test
    public void testProcessItemsAsync() throws Exception {
        List<Item> processedItems = itemService.processItemsAsync().get();

        assertEquals(2, processedItems.size());

        for (Item item : processedItems) {
            assertEquals("PROCESSED", item.getStatus());
        }

        List<Item> itemsFromDb = itemRepository.findAll();
        for (Item item : itemsFromDb) {
            assertEquals("PROCESSED", item.getStatus());
        }
    }
}