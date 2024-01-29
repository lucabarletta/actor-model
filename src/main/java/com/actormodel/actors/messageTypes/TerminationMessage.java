package com.actormodel.actors.messageTypes;

import akka.actor.ActorRef;

public record TerminationMessage(ActorRef targetRef) {
}
