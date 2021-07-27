package ru.geekbrains.cloudservice.commands;

import java.io.Serializable;

public class ResponseMessage implements Serializable {
    private final Response response;
    private AbstractMessage abstractMessage;

    public ResponseMessage(Response response, AbstractMessage abstractMessage) {
        this.response = response;
        this.abstractMessage = abstractMessage;
    }

    public ResponseMessage(Response response) {
        this.response = response;
    }

    public AbstractMessage getAbstractMessageObject() {
        return abstractMessage;
    }

    public Response getResponse() {
        return response;
    }
}
