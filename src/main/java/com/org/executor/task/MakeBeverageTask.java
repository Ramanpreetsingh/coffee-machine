package com.org.executor.task;

import com.org.model.BeverageMachine;

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
