package com.actormodel.actors;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import com.actormodel.actors.messageTypes.ResetSumMessage;
import com.actormodel.config.akkaSpringConfig.ActorComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

@ActorComponent
public class ResettingActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(ResettingActor.class);
    private static final Integer threshold = 100;

    public ResettingActor() {
        getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Integer.class, data -> {
                    if (threshold < data) {
                        getSender().tell(new ResetSumMessage(), ActorRef.noSender());
                        Log.info("thresHold: " + threshold + " reached. Sending reset message to: " + getSender());
                    }
                })
                .match(ReceiveTimeout.class, msg -> getContext().stop(getSelf()))
                .build();
    }

}
