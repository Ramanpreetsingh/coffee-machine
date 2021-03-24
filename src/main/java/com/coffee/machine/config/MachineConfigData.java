package com.coffee.machine.config;

import com.coffee.machine.model.Outlet;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class MachineConfigData {

    @JsonProperty("outlets")
    private Outlet outlets;

    @JsonProperty("total_items_quantity")
    private Map<String, Integer> totalItemsQuantity;

    @JsonProperty("beverages")
    private Map<String, Map<String, Integer>> beverages;
}
