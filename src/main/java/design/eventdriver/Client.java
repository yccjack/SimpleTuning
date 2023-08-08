package design.eventdriver;

public class Client {
    private Event currentEvent;

    private Button button;

    public static void main(String[] args) {
        Button button = new Button();
        button.addEventListener((ClickEventHandler) System.out::println);

        Event currentEvent = new ClickEvent();
        button.notifyListeners(currentEvent);
    }
}
