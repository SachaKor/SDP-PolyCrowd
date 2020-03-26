package ch.epfl.polycrowd.firebase.handlers;

import ch.epfl.polycrowd.Event;

public interface EventHandler {
    void handle(Event event);
}
