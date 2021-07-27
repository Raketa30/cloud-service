package ru.geekbrains.cloudservice.commands;

import java.io.Serializable;

public class RequestMessage  implements Serializable {
    private final Request request;
    private final AbstractMessage abstractMessage;

    public RequestMessage(Request request, AbstractMessage abstractMessage) {
        this.request = request;
        this.abstractMessage = abstractMessage;
    }

    public AbstractMessage getAbstractMessageObject() {
        return abstractMessage;
    }

    public Request getRequest() {
        return request;
    }
}
