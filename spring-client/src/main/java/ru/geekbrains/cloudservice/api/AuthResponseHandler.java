package ru.geekbrains.cloudservice.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.geekbrains.cloudservice.commands.Response;
import ru.geekbrains.cloudservice.commands.auth.AuthResponseType;
import ru.geekbrains.cloudservice.dto.UserTo;
import ru.geekbrains.cloudservice.service.AuthService;

@Service
@Slf4j
public class AuthResponseHandler {
    private final AuthService authService;

    @Autowired
    public AuthResponseHandler(AuthService authService) {
        this.authService = authService;
    }

    public void processHandler(Response<UserTo, AuthResponseType> response) {
        switch (response.getResponseType()) {
            case LOGIN_OK:
                /*
                 * Отправляем в сервис тело трансферобжекта
                 * */
                authService.confirmLoginRequest(response.getResponseBody());
                log.info("recieved {}", response.getResponseBody().toString());
                break;

            case LOGIN_WRONG:
                authService.declineLoginRequest();
                break;

            case REGISTRATION_OK:
                authService.confirmRegistration(response.getResponseBody() );
                String username = response.getResponseBody().getUsername();
                log.info("Registered new user: {}", username);
                break;

            case REGISTRATION_WRONG_USER_EXIST:
                authService.declineRegistration();
                break;
        }
    }
}
