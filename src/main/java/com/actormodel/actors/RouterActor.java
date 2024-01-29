package com.actormodel.actors;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.actormodel.actors.messageTypes.ValueMessage;
import com.actormodel.akkaSpringConfig.ActorComponent;
import com.actormodel.akkaSpringConfig.SpringAkkaExtension;
import com.actormodel.actors.messageTypes.TerminationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ActorComponent
public class RouterActor extends AbstractActor {
    private static final Logger Log = LoggerFactory.getLogger(RouterActor.class);
    private final Map<String, ActorRef> actorRegistry = new ConcurrentHashMap<>();

    private final String EVEN_COUNTING_ACTOR_KEY = "evenCountingActor";
    private final String ODD_COUNTING_ACTOR_KEY = "oddCountingActor";

    public RouterActor() {

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ValueMessage.class, data -> {
                    if (data.value() % 2 == 0) {
                        getRef(EVEN_COUNTING_ACTOR_KEY, EvenCountingActor.class).tell(data, ActorRef.noSender());
                    } else {
                        getRef(ODD_COUNTING_ACTOR_KEY, OddCountingActor.class).tell(data, ActorRef.noSender());
                    }
                }).match(TerminationMessage.class, action -> {
                    actorRegistry.remove(action.targetRef().path().name(), action.targetRef());
                    Log.info("removing ActorRef from registry: " + action.targetRef().path().name());
                })
                .build();
    }

    private ActorRef getRef(String actorKey, Class actor) {
        return actorRegistry.computeIfAbsent(actorKey, id -> {
            Log.info("create " + actorKey + " Actor");
            return getContext()
                    .actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(getContext().getSystem())
                            .props(actor), actorKey);
        });
    }
}
