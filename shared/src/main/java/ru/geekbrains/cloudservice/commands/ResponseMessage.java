package ru.geekbrains.cloudservice.commands;

public class ResponseMessage extends Message {
    private final Response response;
    private AbstractMessage abstractMessage;

    public ResponseMessage(Response response, AbstractMessage abstractMessage) {
        this.response = response;
        this.abstractMessage = abstractMessage;
    }

    public ResponseMessage(Response response) {
        this.response = response;
    }

    @Override
    public AbstractMessage getAbstractMessageObject() {
        return abstractMessage;
    }

    public Response getResponse() {
        return response;
    }
}
