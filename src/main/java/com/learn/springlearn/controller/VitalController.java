package com.learn.springlearn.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.learn.springlearn.SpringlearnApplication.strList;

@RestController
@RequestMapping("/vital")
public class VitalController {

    @PostMapping("/{str}")
    public List<String> postH(@PathVariable String str){
        strList.add(str);
        return getH();
    }

    @GetMapping
    public List<String> getH(){
        return strList;
    }
}
