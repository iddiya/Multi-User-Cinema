package com.ecinema.app.configs;

import com.ecinema.app.beans.SecurityContext;
import com.ecinema.app.configs.interceptors.ModelAttributesInterceptor;
import com.ecinema.app.configs.interceptors.UserActivityInterceptor;
import com.ecinema.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * https://github.com/eugenp/tutorials/blob/master/spring-security-modules/
 * spring-security-web-mvc-custom/src/main/java/com/baeldung/spring/MvcConfig.java
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserService userService;
    private final SecurityContext securityContext;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/about").setViewName("about");
        registry.addViewController("/error").setViewName("error");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/login-success").setViewName("login-success");
        registry.addViewController("/logout-success").setViewName("logout-success");
        registry.addViewController("/message-page").setViewName("message-page");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ModelAttributesInterceptor(
                userService, securityContext));
        registry.addInterceptor(new UserActivityInterceptor(
                userService, securityContext));
    }

}
