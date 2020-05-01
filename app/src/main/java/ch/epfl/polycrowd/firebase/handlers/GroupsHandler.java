package ch.epfl.polycrowd.firebase.handlers;

import java.util.List;

import ch.epfl.polycrowd.logic.Group;

public interface GroupsHandler {
    void handle(List<Group> groups);
}
