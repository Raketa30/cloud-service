package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.cloudservice.commands.ResponseMessage;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.service.ClientAuthService;

@Component
@Slf4j
public class AuthResponseHandler {
    @Autowired
    private ClientAuthService clientAuthService;

    public void processHandler(ResponseMessage message) {
        AuthResponseType command = (AuthResponseType) message.getResponse().getResponseType();

        switch (command) {
            case LOGIN_OK:
                /*
                 * Отправляем в сервис тело трансферобжекта
                 * */
                UserTo user = (UserTo)message.getAbstractMessageObject();
                clientAuthService.confirmLoginRequest(user);
                log.info("recieved {}", user);
                break;

            case LOGIN_WRONG:
                log.info("login wrong respons");
                clientAuthService.declineLoginRequest();
                break;

            case REGISTRATION_OK:
                clientAuthService.confirmRegistration();
                UserTo registered_user = (UserTo)message.getAbstractMessageObject();
                log.info("Registered new user: {}", registered_user.getUsername());
                break;

            case REGISTRATION_WRONG_USER_EXIST:
                clientAuthService.declineRegistration();
                break;
        }
    }
}
