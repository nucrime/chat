package by.ak.chat.component;

import by.ak.chat.model.ChatMessage;
import by.ak.chat.model.Storage;
import by.ak.chat.service.ChatService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class MessageEditor extends VerticalLayout implements KeyNotifier {
  private final ChatService chatService;
  private ChatMessage message;
  private final Storage storage;

  private TextField text = new TextField("Edit message");

  private Button save = new Button("Save", VaadinIcon.CHECK.create());
  private Button undo = new Button("Undo", VaadinIcon.BACKSPACE.create());
  private Button cancel = new Button("Cancel", VaadinIcon.CLOSE_SMALL.create());
  private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
  private HorizontalLayout actions = new HorizontalLayout(save, undo, cancel, delete);

  private Binder<ChatMessage> binder = new Binder<>(ChatMessage.class);
  private ChangeHandler changeHandler;

  public MessageEditor(ChatService chatService, Storage storage) {
    this.chatService = chatService;
    this.storage = storage;

    HorizontalLayout textContainer = new HorizontalLayout();
    text.setWidthFull();
    textContainer.addAndExpand(text);
    add(textContainer, actions);

    binder.bindInstanceFields(this);

    setSpacing(true);

    save.getElement().getThemeList().add("primary");
    delete.getElement().getThemeList().add("error");

    addKeyPressListener(Key.ENTER, e -> save());

    save.addClickListener(e -> save());
    delete.addClickListener(e -> delete());
    undo.addClickListener(e -> editMessage(message));
    cancel.addClickListener(e -> cancel());
    setVisible(false);
  }

  public void cancel() {
    editMessage(message); // set the original value before changing visibility of form
    setVisible(false);
  }

  void delete() {
    chatService.delete(message);
    storage.removeMessage(message);
    changeHandler.onChange();
  }

  void save() {
    chatService.save(message).subscribe();
    storage.updateMessage(message);
    changeHandler.onChange();
  }

  public interface ChangeHandler {
    void onChange();
  }

  public final void editMessage(ChatMessage m) {
    // if msg is null, don't show form and actions
    if (m == null) {
      setVisible(false);
      return;
    }
    final boolean persisted = m.getId() != null;
    if (persisted) {
      // Find entity for editing
      message = chatService.findById(m.getId()).get();
    } else {
      message = m;
    }
    undo.setVisible(persisted);

    // Bind customer properties to similarly named fields
    // Could also use annotation or "manual binding" or programmatically
    // moving values from fields to entities before saving
    binder.setBean(message);

    setVisible(true);

    // Focus first name initially
    text.focus();
  }

  public void setChangeHandler(ChangeHandler h) {
    // ChangeHandler is notified when either save or delete
    // is clicked
    changeHandler = h;
  }
}