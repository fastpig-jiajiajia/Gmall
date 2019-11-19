package com.gmalll.cart.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class CartController {

    @RequestMapping("cart")
    public ModelAndView getCart(){


        return new ModelAndView("redirect: success.html");
    }
}
