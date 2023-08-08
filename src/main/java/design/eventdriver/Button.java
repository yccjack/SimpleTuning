package design.eventdriver;

import java.util.LinkedList;
import java.util.List;

public class Button implements EventSource{

    protected List<EventListener<? extends Event>> listeners  = new LinkedList<>();
    @Override
    public void addEventListener(EventListener<? extends Event> eventListener) {
        listeners.add(eventListener);
    }

    @Override
    public void removeEventListener(EventListener<? extends Event> eventListener) {
        listeners.remove(eventListener);
    }

    @Override
    public void notifyListeners(Event event) {
        for (EventListener listener : listeners) {
            listener.handlerEvent(event);
        }
    }
}
