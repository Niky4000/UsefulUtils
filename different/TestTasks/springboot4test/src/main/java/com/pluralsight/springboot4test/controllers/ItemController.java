package com.pluralsight.springboot4test.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author me
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    @GetMapping
    Long getItemById() {
        return 0L;
    }
}
