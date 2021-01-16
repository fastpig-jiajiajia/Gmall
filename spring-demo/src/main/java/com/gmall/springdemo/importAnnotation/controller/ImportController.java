package com.gmall.springdemo.importAnnotation.controller;

import com.gmall.springdemo.importAnnotation.ImportDemo1;
import com.gmall.springdemo.importAnnotation.ImportDemo2;
import com.gmall.springdemo.importAnnotation.ImportDemo3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author rui.xu
 * @version 1.0.0
 * @date 2020-12-13 23:06:14
 * @description
 */
@RestController
public class ImportController {

    @Autowired
    private ImportDemo1 importDemo1;

    @Autowired
    private ImportDemo2 importDemo2;

    @Autowired
    private ImportDemo3 importDemo3;

    @GetMapping("/importController")
    public String print() {
        importDemo1.print();
        importDemo2.print();
        importDemo3.print();

        return "OK";
    }


}
