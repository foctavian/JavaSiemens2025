package com.siemens.internship;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemServiceTest {
    @Autowired
    private ItemRepository itemRepository;
    private ItemService itemService;

    @BeforeEach
    public void setup() {
        itemService = new ItemService();
    }

    @Test
    public void testSaveItem(){
        Item item = new Item();
        item.setName("Test name");
        item.setDescription("Test description");
        item.setStatus("Test status");
        item.setEmail("test@email.com");
        Item savedItem = itemRepository.save(item);
        assertNotNull(savedItem);
        assertEquals(item.getName(), savedItem.getName());
    }

    @Test
    public void testGetItem(){
        Item item = itemRepository.save(
                Item.builder()
                        .description("test")
                        .name("test")
                        .status("test")
                        .email("test@test.com")
                        .build()
        );

        assertNotNull(item);
        Item fetchedItem = itemRepository.findById(item.getId()).orElse(null);
        assertNotNull(fetchedItem);
        assertEquals(item.getId(), fetchedItem.getId());
    }

    @Test
    public void testDeleteItem(){
        Item item = itemRepository.save(
                Item.builder()
                        .description("test")
                        .name("test")
                        .status("test")
                        .email("test@test.com")
                        .build()
        );

        assertNotNull(itemRepository.findById(item.getId()).orElse(null));
        itemRepository.delete(item);
        assertNull(itemRepository.findById(item.getId()).orElse(null));
    }

    @Test
    public void testUpdateItem(){
        Item item = itemRepository.save(
                Item.builder()
                        .description("test")
                        .name("test")
                        .status("test")
                        .email("test@test.com")
                        .build()
        );
        assertNotNull(itemRepository.findById(item.getId()).orElse(null));
        item.setName("Updated name");
        itemRepository.save(item);
        assertEquals(item.getName(), Objects.requireNonNull(itemRepository.findById(item.getId()).orElse(null)).getName());
    }
}
