package com.coffee.machine;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.coffee.machine.model.BeverageMachine;
import org.apache.commons.io.FileUtils;

public class Application {

    public static void main(String[] args) throws IOException {

        File file = new File(Application.class.getClassLoader().getResource("input.json").getFile());
        String jsonInput = FileUtils.readFileToString(file, "UTF-8");

        BeverageMachine beverageMachine = BeverageMachine.getInstance(jsonInput);
        beverageMachine.makeBeverageParallel(Arrays.asList("hot_tea", "hot_coffee", "green_tea", "black_tea"));
        beverageMachine.shutdownMachine();
    }
}
