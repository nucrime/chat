package by.ak.chat;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {
  private final Storage storage;

  public MainView(Storage storage) {
    this.storage = storage;

    var grid = new Grid<Storage.ChatMessage>();
    grid.setItems(storage.getMessages());
    grid.addColumn(new ComponentRenderer<>(message -> new Html(message.getMessage())))
        .setAutoWidth(true);
    var textField = new TextField();
    textField.setAutofocus(true);
    add(
        new H3("Chat"),
        grid,
        textField,
        new Button("Send message") {
          {
            addClickListener(
                click -> {
                  storage.addMessage("", textField.getValue());
                  textField.clear();
                });
            addClickShortcut(Key.ENTER);
          }
        });
  }
}
