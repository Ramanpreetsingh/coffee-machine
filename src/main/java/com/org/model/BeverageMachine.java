package com.org.model;

import com.org.executor.task.MakeBeverageTask;
import com.org.config.MachineConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents a Beverage Machine with n outlets. The machine processes make beverage requests in
 * multi threaded manner with n threads at a time.
 */

@Getter
public class BeverageMachine {

    private ExecutorService executor;

    /**
     * This solution assumes that the input JSON is a valid JSON, without duplicate beverages. If the JSON is not valid
     * then the application startup would fail. Ideally the config should be stored in a datastore, to avoid
     * configuring the system in application startup.
     */

    private BeverageMachine(final String jsonInput){

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MachineConfig machineConfig = objectMapper.readValue(jsonInput, MachineConfig.class);
            initialiseMachineContents(machineConfig);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid JSON data");
        }
    }

    private static BeverageMachine instance;

    /**
     * Known issues: If two threads try to initialise the beverage machine with different configs, the thread which gets
     * to synchronized block first will initialise the machine with it's config, and the second thread will be using
     * the config initialised by the first thread. Ideally a singleton should be created without any parameters.
     */

    public static synchronized BeverageMachine getInstance(String jsonInput){
        if(instance == null){
            synchronized (BeverageMachine.class) {
                if(instance == null){
                    instance = new BeverageMachine(jsonInput);
                }
            }
        }
        return instance;
    }

    // Map to store status of contents in the machine
    private Map<String, Integer> machineContents;

    private Map<String, Map<String, Integer>> beverageConfig;

    private Map<String, Integer> beveragesProduced;

    public void makeBeverageParallel(List<String> beverages){
        beverages
                .stream()
                .map(it -> new MakeBeverageTask(this, it))
                .forEach(task -> executor.execute(task));
    }

    private void initialiseMachineContents(MachineConfig machineConfig){
        machineContents = machineConfig.getMachineConfigData().getTotalItemsQuantity();
        beverageConfig = machineConfig.getMachineConfigData().getBeverages();
        int count = machineConfig.getMachineConfigData().getOutlets().getCount();
        executor = Executors.newFixedThreadPool(count);
        beveragesProduced = new HashMap();
    }

    /**
     * This method is made synchronized to ensure that no two make beverage requests (tow threads) try to access the
     * same content at the same time.
     * @param beverageName
     */
    public synchronized void makeBeverage(String beverageName){

        Map<String, Integer> beverage;

        if(beverageConfig.containsKey(beverageName)){
            beverage = beverageConfig.get(beverageName);
        }else{
            System.out.println(beverageName + " cannot be prepared because config is not available");
            return;
        }

        for(Map.Entry<String, Integer> entry: beverage.entrySet()) {
            if (machineContents.get(entry.getKey()) != null) {
                if (machineContents.get(entry.getKey()) < entry.getValue()) {
                    System.out.println(beverageName + " cannot be prepared because " + entry.getKey() + " is not " +
                            "available");
                    return;
                }
            } else {
                System.out.println(beverageName + " cannot be prepared because " + entry.getKey() + " is not " +
                        "available");
                return;
            }
        }

        for(Map.Entry<String, Integer> entry: beverage.entrySet()){
            machineContents.put(entry.getKey(),machineContents.get(entry.getKey()) - entry.getValue());
        }

        beveragesProduced.put(beverageName, beveragesProduced.getOrDefault(beverageName, 0 ) + 1);

        System.out.println(beverageName + " is prepared");
    }

    public synchronized void refillContent(String contentType, Integer quantity){
        machineContents.put(contentType, machineContents.getOrDefault(contentType, 0) + quantity);
    }

    public void shutdownMachine(){
        executor.shutdown();
    }

    public void getLowContents(){
    }

}
