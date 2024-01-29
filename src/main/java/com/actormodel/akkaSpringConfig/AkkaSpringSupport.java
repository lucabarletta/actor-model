package com.actormodel.akkaSpringConfig;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import org.apache.camel.tooling.model.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class AkkaSpringSupport {
    private final Logger Log = LoggerFactory.getLogger(AkkaSpringSupport.class);

    @Autowired
    private ActorSystem actorSystem;

    protected ActorRef actorOf(Class<? extends Actor> actorClass, String mailBoxName, String actorName) {
        if (Strings.isNullOrEmpty(actorName)) {
            Log.error("ActorName ");
            return null;
        }
        if (!mailBoxName.isEmpty()) {
            return actorSystem.actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(actorSystem).props(actorClass).withMailbox(mailBoxName), actorName);
        } else {
            return actorSystem.actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(actorSystem).props(actorClass), actorName);
        }
    }

    protected ActorSelection actorSelection(String path) {
        return actorSystem.actorSelection(path);
    }
}
