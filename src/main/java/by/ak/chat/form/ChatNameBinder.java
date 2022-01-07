package by.ak.chat.form;

import by.ak.chat.model.ChatMessage;
import by.ak.chat.security.SecurityService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;

public class ChatNameBinder {

  private static final String CREATION_SUCCESS_MSG = "Chat %s created successfully, welcome %s";
  private static final String CHAT_NAME_FIELD = "chat";
  private final ShowCreateChatForm showCreateChatForm;
  private final SecurityService securityService;

  public ChatNameBinder(ShowCreateChatForm showCreateChatForm, SecurityService securityService) {
    this.showCreateChatForm = showCreateChatForm;
    this.securityService = securityService;
  }

  /**
   * Method to add the data binding and validation logics
   * to the registration form
   */
  public void addBindingAndValidation() {
    BeanValidationBinder<ChatMessage> binder = new BeanValidationBinder<>(ChatMessage.class);

    /*
     * Use annotation based validator for chat field, from ChatMessage class
     */

    binder.bind(showCreateChatForm.getNewChatName(), CHAT_NAME_FIELD);

    /*
     * Set the label where bean-level error messages go
     */
    binder.setStatusLabel(showCreateChatForm.getErrorMessageField());

    /*
     * And finally the submit button
     */
    showCreateChatForm.getSubmitButton().addClickListener(event -> {
      try {
        /*
        Create empty bean to store the details in it
        */
        ChatMessage chatMessage = new ChatMessage();

        /*
        Run validators and write the values to the bean
        */
        binder.writeBean(chatMessage);

        /*
         * Typically, you would here call backend to store the bean

         * Show success message if everything went well
         */
        showSuccess(chatMessage);
      } catch (ValidationException exception) {
        /*
         * validation errors are already visible for each field,
         * and bean-level errors are shown in the status label.
         * We could show additional messages here if we want, do logging, etc.
         */
      }
    });
  }

  /**
   * We call this method when form submission has succeeded
   *
   * @param chatMessage
   */
  private void showSuccess(ChatMessage chatMessage) {
    String user = securityService.getLoggedInUserName();
    String chat = chatMessage.getChat();
    Notification notification =
      Notification.show(
        String.format(CREATION_SUCCESS_MSG, chat, user));
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    showCreateChatForm.selectAndNavigate(chat, user);
  }
}
