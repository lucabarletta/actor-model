package com.actormodel.actors;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import com.actormodel.actors.messageTypes.ResetSumMessage;
import com.actormodel.actors.messageTypes.ValueMessage;
import com.actormodel.akkaSpringConfig.ActorComponent;
import com.actormodel.actors.messageTypes.TerminationMessage;
import com.actormodel.akkaSpringConfig.SpringAkkaExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

@ActorComponent
public class OddCountingActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(OddCountingActor.class);
    private final ActorRef resetActor;
    private int oddSum;

    public OddCountingActor() {
        getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
        this.oddSum = 0;
        resetActor = getContext().actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(getContext().getSystem())
                .props(ResettingActor.class), "ResetActor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ValueMessage.class, data -> {
                            oddSum += data.value();
                            Log.info(getContext().getSelf().getClass() + " received value: " + data.value() + " oddSum is: " + oddSum);
                            resetActor.tell(oddSum, self());
                        }
                )
                .match(ReceiveTimeout.class, msg -> onReceiveTimeout())
                .match(ResetSumMessage.class, msg -> {
                    this.oddSum = 0;
                })
                .build();
    }

    private void onReceiveTimeout() {
        context().parent().tell(new TerminationMessage(self()), ActorRef.noSender());
        getContext().stop(getSelf());
    }
}
