package by.ak.chat.util;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

public class EventBus {
  private static final ComponentEventBus eventBus = new ComponentEventBus(new Div());

  public static <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
    return eventBus.addListener(eventType, listener);
  }

  public static <T extends ComponentEvent<?>> void fireEvent(T event) {
    eventBus.fireEvent(event);
  }
}
