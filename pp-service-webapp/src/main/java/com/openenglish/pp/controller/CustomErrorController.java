package com.openenglish.pp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {

  private static final String PATH = "/error";
  @Autowired
  private ErrorAttributes errorAttributes;
  private final static Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

  @RequestMapping(value = PATH, produces = "text/html")
  public String error(HttpServletRequest aRequest) {
    RequestAttributes requestAttributes = new ServletRequestAttributes(aRequest);
    Throwable error = errorAttributes.getError(requestAttributes);
    Map<String,Object> attributes = errorAttributes.getErrorAttributes(requestAttributes, true);
    logger.error("Error occurred", error);
    StringBuilder builder = new StringBuilder();
    builder.append(attributes.get("timestamp"))
           .append("<BR>")
           .append("There was an unexpected error(type=").append(attributes.get("error"))
           .append(", status=").append(attributes.get("status")).append(")")
           .append("<BR>")
           .append(attributes.get("message"));
    return builder.toString();
  }

  @Override
  public String getErrorPath() {
    return PATH;
  }
}
