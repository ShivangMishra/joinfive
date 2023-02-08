package com.example.joinfive.model.ai.nrpa;

import java.util.HashMap;

public class NrpaPolicy<TAction> {

    private final HashMap<String, Double> actionsMap;

    public NrpaPolicy() {
        actionsMap = new HashMap<>();
    }

    public synchronized double get(TAction tAction) {
        //System.out..println("Get" + tAction);
//        //System.out..println("Contains ? " + actionsMap.containsKey(tAction));
        Double value = actionsMap.get(tAction.toString());
        //System.out..println("value =" + value);
        if(value == null) {
            value = 0.0;
            actionsMap.put(tAction.toString(), value);
            //System.out..println("Put");
        }
        return value;
    }

    public synchronized NrpaPolicy<TAction> copy() {
        //System.out..println("copy");
        NrpaPolicy<TAction> np = new NrpaPolicy<>();
        np.actionsMap.putAll(actionsMap);
        return np;
    }

    public synchronized void set(TAction tAction, double v) {
        //System.out..println("Pushed : " + tAction + "  =>" + v);
        actionsMap.put(tAction.toString(), v);
    }
}
