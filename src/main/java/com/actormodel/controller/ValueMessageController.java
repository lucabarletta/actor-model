package com.actormodel.controller;

import com.actormodel.actors.messageTypes.ValueMessage;
import com.actormodel.services.ActorEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@Component
public class ValueMessageController {
    private volatile boolean running = true;
    private final ActorEntryPoint actorEntryPoint;
    private static final Logger Log = LoggerFactory.getLogger(ValueMessageController.class);

    public ValueMessageController(ActorEntryPoint actorEntryPoint) {
        this.actorEntryPoint = actorEntryPoint;
    }

    @PostMapping("api/start")
    public void start() {
        running = true;
        Random rand = new Random();
        while (running) {
            int randomInt = rand.nextInt(100);
            actorEntryPoint.entryActorSystem(new ValueMessage(randomInt));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.error("Thread was interrupted, Failed to complete operation");
                running = false;
            }
        }
        Log.info("Random Integer Generation Stopped");
    }

    @PostMapping("api/stop")
    public void stop() {
        running = false;
    }
}
