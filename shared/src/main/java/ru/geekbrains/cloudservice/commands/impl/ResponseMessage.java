package ru.geekbrains.cloudservice.commands.impl;

import ru.geekbrains.cloudservice.commands.AbstractMessage;
import ru.geekbrains.cloudservice.commands.Message;
import ru.geekbrains.cloudservice.commands.Response;

public class ResponseMessage implements Message {
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
