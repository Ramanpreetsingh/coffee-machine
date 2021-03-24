package com.coffee.machine.executor.task;

import com.coffee.machine.model.BeverageMachine;

public class MakeBeverageTask implements Runnable {

    private BeverageMachine beverageMachine;

    private String beverageName;

    public MakeBeverageTask(BeverageMachine beverageMachine, String beverageName){
        this.beverageMachine = beverageMachine;
        this.beverageName = beverageName;
    }

    @Override
    public void run() {
        beverageMachine.makeBeverage(beverageName);
    }
}
