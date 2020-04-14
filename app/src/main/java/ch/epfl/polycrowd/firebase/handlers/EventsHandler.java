package ch.epfl.polycrowd.firebase.handlers;

import java.util.List;

import ch.epfl.polycrowd.logic.Event;

public interface EventsHandler {
    void handle(List<Event> events);
}
