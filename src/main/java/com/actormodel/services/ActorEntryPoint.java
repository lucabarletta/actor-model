package com.actormodel.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.actormodel.actors.ResettingActor;
import com.actormodel.akkaSpringConfig.AkkaSpringSupport;
import com.actormodel.akkaSpringConfig.SpringAkkaExtension;
import com.actormodel.actors.RouterActor;
import com.actormodel.actors.messageTypes.ValueMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ActorEntryPoint extends AkkaSpringSupport {
    private final ActorRef routerActor;

    public ActorEntryPoint(ApplicationContext applicationContext) {
        ActorSystem system = applicationContext.getBean(ActorSystem.class);
        routerActor = system.actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(system).props(RouterActor.class), "routerActor");
        ActorRef resetActor = system.actorOf(SpringAkkaExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(ResettingActor.class), "ResetActor");
    }

    public void entryActorSystem(ValueMessage valueMessage) {
        routerActor.tell(valueMessage, ActorRef.noSender());
    }
}
