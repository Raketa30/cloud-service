package ru.geekbrains.cloudservice.commands;

public class RequestMessage extends Message {
    private final Request request;
    private  AbstractMessage abstractMessage;

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
