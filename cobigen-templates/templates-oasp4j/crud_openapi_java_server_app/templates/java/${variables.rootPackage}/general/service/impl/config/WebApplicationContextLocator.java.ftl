package ${variables.rootPackage}.general.service.impl.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Configuration
public class WebApplicationContextLocator implements ServletContextInitializer {

  private static WebApplicationContext webApplicationContext;

  public static WebApplicationContext getCurrentWebApplicationContext() {

    return webApplicationContext;
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {

    webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
  }
}
