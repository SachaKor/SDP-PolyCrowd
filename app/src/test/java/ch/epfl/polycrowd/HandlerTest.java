package ch.epfl.polycrowd;

import org.junit.Test;

import ch.epfl.polycrowd.firebase.EmptyHandler;
import ch.epfl.polycrowd.firebase.Handler;
import static org.junit.Assert.assertTrue;

public class HandlerTest {
    private boolean handled = false;

    private class HandlerTester implements Handler<Integer> {
        @Override
        public void handle(Integer arg) {
            handled = true;
        }
    }

    private class EmptyHandlerTester implements EmptyHandler {

        @Override
        public void handle() {
            handled = false;
        }
    }

    @Test
    public void TestHandler(){
        HandlerTester h = new HandlerTester();
        h.handle(0);
        assertTrue(handled);
    }

    @Test
    public void TestEmptyHandler(){
        EmptyHandlerTester eh = new EmptyHandlerTester();
        eh.handle();
        assertTrue(handled);
    }
}
