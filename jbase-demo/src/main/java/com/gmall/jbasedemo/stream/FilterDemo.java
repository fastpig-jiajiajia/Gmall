package com.gmall.jbasedemo.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilterDemo {

    public static void main(String[] args) {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(3L);
        list.add(4L);
        list.add(5L);

        list = list.stream().filter(e -> e>3).collect(Collectors.toList());

        list.stream().forEach(System.out::println);
    }
}
