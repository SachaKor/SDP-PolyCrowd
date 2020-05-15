package ch.epfl.polycrowd.firebase.handlers;

public interface Handler<E> {

    public void handle(E arg) ;
}
