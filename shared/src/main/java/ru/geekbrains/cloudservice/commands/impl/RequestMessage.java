package ru.geekbrains.cloudservice.commands.impl;

import ru.geekbrains.cloudservice.commands.AbstractMessage;
import ru.geekbrains.cloudservice.commands.Message;
import ru.geekbrains.cloudservice.commands.Request;

public class RequestMessage implements Message {
    private final Request request;
    private AbstractMessage abstractMessage;

    public RequestMessage(Request request, AbstractMessage abstractMessage) {
        this.request = request;
        this.abstractMessage = abstractMessage;
    }

    public RequestMessage(Request request) {
        this.request = request;
    }

    @Override
    public AbstractMessage getAbstractMessageObject() {
        return abstractMessage;
    }

    public Request getRequest() {
        return request;
    }
}
