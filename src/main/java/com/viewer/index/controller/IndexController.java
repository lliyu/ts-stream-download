package com.viewer.index.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping
public class IndexController {

    @RequestMapping("/")
    @ResponseBody
    public String test(){
        return "success";
    }
}
