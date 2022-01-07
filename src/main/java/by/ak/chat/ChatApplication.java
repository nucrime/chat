package by.ak.chat;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@Push
public class ChatApplication  extends SpringBootServletInitializer implements AppShellConfigurator {

  public static void main(String[] args) {
    SpringApplication.run(ChatApplication.class, args);
  }
}
