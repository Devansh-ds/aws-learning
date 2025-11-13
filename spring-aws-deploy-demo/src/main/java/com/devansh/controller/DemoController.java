package com.devansh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    private int counter = 0;

    @GetMapping("/demo")
    public String demo() {
        counter++;
        return String.format("Demo counter: %d", counter);
    }
}
