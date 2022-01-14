package by.ak.chat.view;

import by.ak.chat.component.Header;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.ParentLayout;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletResponse;

// Same as in WhereAmILayout because I don't think it's a good idea to show whether we have requested resource at all
// todo rework, as spring security intercepts faster than vaadin
@ParentLayout(Header.class)
public class ForbiddenLayout extends VerticalLayout implements HasErrorParameter<AccessDeniedException> {
  private Span explanation;

  public ForbiddenLayout() {
    var container = new HorizontalLayout();
    var h1 = new H1("Where am I?");
    explanation = new Span();

    container.add(h1);

    add(container);
    add(explanation);
    setAlignItems(Alignment.CENTER);
  }

  @Override
  public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<AccessDeniedException> parameter) {
    explanation.setText("Could not navigate to '"
      + event.getLocation().getPath() + "'.");
    return HttpServletResponse.SC_NOT_FOUND;
  }
}
