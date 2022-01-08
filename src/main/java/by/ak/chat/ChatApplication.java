package by.ak.chat;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Push
@PWA(name = "FUAGRA", shortName = "FUAGRA", offlineResources = {"/pages/offline.html"})
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class ChatApplication extends SpringBootServletInitializer implements AppShellConfigurator {

  public static void main(String[] args) {
    SpringApplication.run(ChatApplication.class, args);
  }
}
