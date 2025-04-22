package com.doittogether.platform;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tests")
public class TestController {

    @GetMapping
    public String test() throws Exception {

        if(true) throw new Exception("aaaaaaaaaaa");

        return "test";
    }
}
