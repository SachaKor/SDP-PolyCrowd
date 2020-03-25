package ch.epfl.polycrowd;

import java.util.List;

public interface EventsHandler {
    void handle(List<Event> events);
}
