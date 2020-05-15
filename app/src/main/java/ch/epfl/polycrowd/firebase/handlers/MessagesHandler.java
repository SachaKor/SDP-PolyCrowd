package ch.epfl.polycrowd.firebase.handlers;

import java.util.List;

import ch.epfl.polycrowd.logic.Message;

public interface MessagesHandler {
    void handle(List<Message> ms);
}
