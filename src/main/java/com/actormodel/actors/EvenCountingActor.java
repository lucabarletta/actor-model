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
public class EvenCountingActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(EvenCountingActor.class);
    private final ActorRef resetActor;
    private int evenSum;

    public EvenCountingActor() {
        getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
        this.evenSum = 0;
        resetActor = getContext().actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(getContext().getSystem())
                .props(ResettingActor.class), "ResetActor");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ValueMessage.class, data -> {
                            evenSum += data.value();
                            Log.info(getContext().getSelf().getClass() + " received value: " + data.value() + " evenSum is: " + evenSum);
                            resetActor.tell(evenSum, self());
                        }
                )
                .match(ReceiveTimeout.class, msg -> onReceiveTimeout())
                .match(ResetSumMessage.class, msg -> {
                    this.evenSum = 0;
                })
                .build();
    }

    private void onReceiveTimeout() {
        context().parent().tell(new TerminationMessage(self()), ActorRef.noSender());
        getContext().stop(getSelf());
    }
}
