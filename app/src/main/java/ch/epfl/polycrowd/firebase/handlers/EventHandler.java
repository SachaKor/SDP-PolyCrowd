package ch.epfl.polycrowd.firebase.handlers;

import ch.epfl.polycrowd.logic.Event;

public interface EventHandler {
    void handle(Event event);
}
