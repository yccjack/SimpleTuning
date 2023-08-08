package design.eventdriver;

public interface EventListener <T extends Event>{

    void handlerEvent(T event);
}
