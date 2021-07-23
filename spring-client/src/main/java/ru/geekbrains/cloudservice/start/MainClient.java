package ru.geekbrains.cloudservice.start;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ru.geekbrains.cloudservice.CloudServiceApplication;
import ru.geekbrains.cloudservice.controller.AuthController;

public class MainClient extends Application{
    private ConfigurableApplicationContext configurableApplicationContext;

    @Override
    public void init() throws Exception {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.configurableApplicationContext = new SpringApplicationBuilder()
                .sources(CloudServiceApplication.class).run(args);
    }

    @Override
    public void stop() throws Exception {
        this.configurableApplicationContext.close();
        Platform.exit();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FxWeaver fxWeaver = configurableApplicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(AuthController.class);
        stage.setTitle("version 1.0");
        stage.setMinWidth(650);
        stage.setMinHeight(400);
        stage.setResizable(false);
        stage.setScene(new Scene(root, 650, 400));

        stage.show();

    }
}
