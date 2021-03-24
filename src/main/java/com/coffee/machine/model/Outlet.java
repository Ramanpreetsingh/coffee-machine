package com.coffee.machine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Outlet {

    @JsonProperty("count_n")
    private int count;
}
