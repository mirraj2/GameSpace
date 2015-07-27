package gamespace.web.notfound;

import jasonlib.IO;
import jasonlib.Log;
import bowser.Request;
import bowser.RequestHandler;
import bowser.Response;
import bowser.handler.StaticContentHandler;
import bowser.template.Context;
import bowser.template.Template;

public class PageNotFoundHandler implements RequestHandler {

  private final Template template;
  
  public PageNotFoundHandler(StaticContentHandler loader) {
    template = Template.compile(IO.from(getClass(), "notfound.html").toString(), loader);
  }
  
  @Override
  public boolean process(Request request, Response response) {
    if (request.isStaticResource()) {
      return false;
    }

    if (!request.getMethod().equalsIgnoreCase("GET")) {
      return false;
    }

    String html = template.render(new Context(request, response));
    response.write(html);

    Log.warn("404: " + request);

    return true;
  }

}
