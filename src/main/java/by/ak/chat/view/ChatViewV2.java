package by.ak.chat.view;

import by.ak.chat.security.SecurityService;
//import com.vaadin.collaborationengine.CollaborationAvatarGroup;
//import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("chatV2")
@PageTitle("Chat V2")
@Push
public class ChatViewV2 extends VerticalLayout {

//  private final UserInfo userInfo;
  private final SecurityService securityService;

  public ChatViewV2(SecurityService securityService) {
    this.securityService = securityService;
//    this.userInfo = new UserInfo(securityService.getLoggedInUserName());

    addAndExpand(getHeader());
    addAndExpand(getMessageList());
  }

  private Component getHeader() {
    HorizontalLayout header = new HorizontalLayout();
    header.setWidthFull();
    header.setAlignItems(HorizontalLayout.Alignment.BASELINE);

//    CollaborationAvatarGroup avatars = new CollaborationAvatarGroup(userInfo, "avatars");

    H1 title = new H1("Chat");

//    header.add(title, avatars);
    header.addAndExpand(title);
    return header;
  }

  private Component getMessageList() {
    MessageList messageList = new MessageList();
    messageList.setWidthFull();
    return messageList;
  }
}
