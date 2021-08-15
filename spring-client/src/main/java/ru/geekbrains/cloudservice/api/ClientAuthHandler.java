package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.commands.impl.ResponseMessage;
import ru.geekbrains.cloudservice.commands.impl.auth.AuthResponseType;
import ru.geekbrains.cloudservice.service.ClientAuthService;

@Component
@Slf4j
public class ClientAuthHandler {
    @Autowired
    private ClientAuthService clientAuthService;

    public void processHandler(ResponseMessage message) {
        AuthResponseType command = (AuthResponseType) message.getResponse().getResponseType();

        switch (command) {
            case LOGIN_OK:
                clientAuthService.confirmLoginRequest(message);
                break;

            case LOGIN_WRONG:
                clientAuthService.declineLoginRequest();
                break;

            case REGISTRATION_OK:
                clientAuthService.confirmRegistration(message);
                break;

            case REGISTRATION_WRONG_USER_EXIST:
                clientAuthService.declineRegistration();
                break;
        }
    }
}
