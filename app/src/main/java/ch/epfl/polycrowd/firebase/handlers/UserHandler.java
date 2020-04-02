package ch.epfl.polycrowd.firebase.handlers;

import ch.epfl.polycrowd.logic.User;

/* used to handle the successfully logged in user */
public interface UserHandler {
    void handle(User user);
}
