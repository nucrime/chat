package by.ak.chat.component;

import by.ak.chat.exception.AnotherUserWithUsernameExists;
import by.ak.chat.model.Role;
import by.ak.chat.model.User;
import by.ak.chat.service.UserService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.server.AbstractStreamResource;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Cleanup;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;


@Component
@UIScope
public class UserEditor extends VerticalLayout implements KeyNotifier {
  private final UserService userService;
  private TextField username = new TextField("Username");
  private TextField email = new TextField("Email");
  private TextField firstName = new TextField("First name");
  private TextField lastName = new TextField("Last name");
  private ComboBox<Role> role = new ComboBox<>("Role");
  private TextField password = new TextField("Password");
  private DatePicker dob = new DatePicker("Date of birth");
  private Avatar avatar = new Avatar("Avatar");
  private User user;
  private Button save = new Button("Save", VaadinIcon.CHECK.create());
  private Button cancel = new Button("Cancel", VaadinIcon.CLOSE_SMALL.create());
  private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
  private Button ban = new Button("Ban/Unban", VaadinIcon.CLOSE_CIRCLE.create());
  private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete, ban);

  private Binder<User> binder = new Binder<>(User.class);
  private ChangeHandler changeHandler;

  public UserEditor(UserService userService) {
    this.userService = userService;

    var formLayout = new FormLayout();
    formLayout.add(username, email, firstName, lastName, role, password, dob);

    role.setItems(Role.values());
    Upload upload = getUploadComponent();

    var avatarRow = new HorizontalLayout();
    avatarRow.add(avatar, upload);

    var actionsRow = new HorizontalLayout();
    actionsRow.add(actions);

    add(formLayout, avatarRow, actionsRow);
    this.setAlignItems(Alignment.CENTER);

    // bind using naming convention
    binder.bindInstanceFields(this);

    // Configure and style components
    setSpacing(true);

    save.getElement().getThemeList().add("primary");
    delete.getElement().getThemeList().add("error");
    ban.getElement().getThemeList().add("error");

    // wire action buttons to save, delete and reset
    save.addClickListener(e -> save());
    addKeyPressListener(Key.ENTER, e -> save());
    delete.addClickListener(e -> delete());
    ban.addClickListener(e -> ban());
    cancel.addClickListener(e -> editCustomer(user));

    setVisible(false);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    if (Objects.nonNull(user.getAvatar())) {
      avatar.setImageResource(streamResourceForBytes("nobodycares", user.getAvatar()));
    }
  }

  private Upload getUploadComponent() {
    MemoryBuffer buffer = new MemoryBuffer();
    Upload upload = new Upload(buffer);
    upload.setMaxFiles(1);
    upload.setDropLabel(new Label("Drop avatar here"));
    upload.setAcceptedFileTypes("image/jpeg", ".jpg", ".jpeg");

    upload.addSucceededListener(event -> {
      // Get information about the uploaded file
      InputStream fileData = buffer.getInputStream();
      String fileName = event.getFileName();
      long contentLength = event.getContentLength();
      String mimeType = event.getMIMEType();

      // Do something with the file data
      processFile(fileData, fileName, contentLength, mimeType);
    });

    upload.addFileRejectedListener(event -> {
      String errorMessage = event.getErrorMessage();

      Notification notification = Notification.show(
        errorMessage,
        Duration.ofSeconds(5).toMillisPart(),
        Notification.Position.MIDDLE
      );
      notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    });
    return upload;
  }

  @SneakyThrows
  private void processFile(InputStream fileData, String fileName, long contentLength, String mimeType) {
    var resized = resizeImage(ImageIO.read(fileData));
    AbstractStreamResource resource = streamResourceForBytes(fileName, resized);
    user.setAvatar(resized);

    avatar.setImageResource(resource);
  }

  @SneakyThrows
  private AbstractStreamResource streamResourceForBytes(String fileName, byte[] bytes) {
    @Cleanup var stream = new ByteArrayInputStream(bytes);
    return new StreamResource(fileName, () -> stream);
  }

  private byte[] resizeImage(BufferedImage originalImage) throws Exception {
    @Cleanup ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // Resize the image
    Thumbnails.of(originalImage)
      .size(100, 100)
      .outputFormat("JPEG")
      .outputQuality(1)
      .toOutputStream(outputStream);
    // Return the resized image as byte array
    byte[] data = outputStream.toByteArray();
    // May be Below double transformation is not needed. It's not the case when base 64 is required.
    // so why add this information?
    @Cleanup ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
    // ImageIO.read/write is automatically closed
    BufferedImage read = ImageIO.read(inputStream);
    ImageIO.write(read, "image/jpeg;base64", outputStream);
    return outputStream.toByteArray();
  }

  private void ban() {
    userService.banOrUnban(user);
    changeHandler.onChange();
  }

  void delete() {
    userService.delete(user);
    changeHandler.onChange();
  }

  void save() {
    try {
      userService.save(user);
      changeHandler.onChange();
    } catch (AnotherUserWithUsernameExists e) {
      Notification.show("User with this username already exists");
    }
  }

  public final void editCustomer(User u) {
    // if user is null, don't show form and actions
    if (u == null) {
      setVisible(false);
      return;
    }
    final boolean persisted = u.getId() != null;
    if (persisted) {
      // Find fresh entity for editing
      user = userService.findById(u.getId()).get();
    } else {
      user = u;
    }
    ban.setVisible(!Objects.equals(userService.currentUserRole(), Role.USER.name())); // user can not ban

    cancel.setVisible(persisted);

    // Bind customer properties to similarly named fields
    // Could also use annotation or "manual binding" or programmatically
    // moving values from fields to entities before saving
    binder.setBean(user);

    setVisible(true);

    // Focus first name initially
    firstName.focus();
  }

  public void setChangeHandler(ChangeHandler h) {
    // ChangeHandler is notified when either save or delete
    // is clicked
    changeHandler = h;
  }

  public interface ChangeHandler {
    void onChange();
  }
}
