package by.ak.chat.view;

import by.ak.chat.component.Header;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.ParentLayout;

import javax.servlet.http.HttpServletResponse;

@ParentLayout(Header.class)
public class WhereAmILayout extends VerticalLayout implements HasErrorParameter<NotFoundException> {
  private final Header header;

  private Span explanation;

  public WhereAmILayout(Header header) {
    this.header = header;
    var container = new HorizontalLayout();
    var h1 = new H1("Where am I?");
    explanation = new Span();

    container.add(h1);

    add(header.init());
    add(container);
    add(explanation);
    setAlignItems(Alignment.CENTER);
  }

  @Override
  public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
    explanation.setText("Could not navigate to '"
      + event.getLocation().getPath() + "'.");
    return HttpServletResponse.SC_NOT_FOUND;
  }
}
