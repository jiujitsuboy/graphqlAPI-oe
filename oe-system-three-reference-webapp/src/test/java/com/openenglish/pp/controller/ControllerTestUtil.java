package com.openenglish.pp.controller;


import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;

public class ControllerTestUtil {

  private static ExceptionHandlerExceptionResolver createExceptionResolver() {
    ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
      protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod,
                                                                        Exception exception) {
        Method method = new ExceptionHandlerMethodResolver(MyGlobalExceptionHandler.class)
                .resolveMethod(exception);
        return new ServletInvocableHandlerMethod(new MyGlobalExceptionHandler(), method);
      }
    };
    exceptionResolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    exceptionResolver.afterPropertiesSet();
    return exceptionResolver;
  }

  public static MockMvc buildMockMvc(Object controller) {
    return MockMvcBuilders.standaloneSetup(controller).setHandlerExceptionResolvers(
        createExceptionResolver()).build();
  }


  public static String buildUrlFromPath(String path, String... params) {
    return UriComponentsBuilder.fromPath(path)
        .build()
        .expand(params).toString();
  }
}
