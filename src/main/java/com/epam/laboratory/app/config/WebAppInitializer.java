package com.epam.laboratory.app.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
        webAppContext.register(AppConfig.class, WebConfig.class, PersistenceConfig.class);
        servletContext.addListener(new ContextLoaderListener(webAppContext));

        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcherServlet", new DispatcherServlet(webAppContext));
        dispatcher.addMapping("/");
        dispatcher.setLoadOnStartup(1);

        webAppContext.setServletContext(servletContext);
    }

}
