package design.eventdriver;

public interface EventSource {

    void addEventListener(EventListener<? extends Event> eventListener);
    void removeEventListener(EventListener<? extends Event> eventListener);
    void notifyListeners(Event event);
}
