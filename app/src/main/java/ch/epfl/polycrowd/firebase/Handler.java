package ch.epfl.polycrowd.firebase;

public interface Handler<E> {

    void handle(E arg);
}
