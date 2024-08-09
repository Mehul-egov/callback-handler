package org.qwikpe.callback.handler.enums;

public enum Action {
    search,
    init,
    confirm,
    cancel,
    status,
    on_search,
    on_init,
    on_confirm,
    on_cancel,
    on_status,
    on_message;

    Action() {}
    Action(Action action) {}

    public static Action getAction(String action) {

        if(action == null) return null;

        for(Action a : Action.values()) {
            if(a.name().equalsIgnoreCase(action)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Invalid Status input: " + status);
    }
}
