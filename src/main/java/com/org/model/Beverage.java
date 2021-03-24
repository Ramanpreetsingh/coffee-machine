package com.org.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Beverage {

    private String name;

    private Map<String, Integer> contents;
}
