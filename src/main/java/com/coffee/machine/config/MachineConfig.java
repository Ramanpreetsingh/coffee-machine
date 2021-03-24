package com.coffee.machine.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MachineConfig {

    @JsonProperty("machine")
    private MachineConfigData machineConfigData;

}
