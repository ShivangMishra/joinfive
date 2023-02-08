package com.example.joinfive.model.ai;

public interface AlgorithmObserver {
    void done();

    void updateTimeElapsed(long timeElapsed);
}
