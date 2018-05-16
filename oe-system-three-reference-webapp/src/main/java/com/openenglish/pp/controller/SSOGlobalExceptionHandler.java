package com.openenglish.pp.controller;

import com.openenglish.sso.common.api.model.ErrorResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;

@ControllerAdvice
public  class SSOGlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(SSOGlobalExceptionHandler.class);

  @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class,
                     MissingServletRequestParameterException.class})
  public ResponseEntity handleBadRequest(HttpServletRequest request, Exception e) {
    logger.error("Bad request ", e);
    return new ResponseEntity(new ErrorResponse().setErrorCode(
        HttpStatus.BAD_REQUEST.value()).setErrorMessage(e.getMessage())
        , HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ResourceNotFoundException.class})
  public ResponseEntity handleResourceNotFound(HttpServletRequest request, Exception e) {
    return new ResponseEntity(new ErrorResponse().setErrorCode(HttpStatus.NOT_FOUND.value()).setErrorMessage(e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity handle(HttpServletRequest request, Exception e) {
    logger.error("Failed to handle request for url: "+getFullURL(request), e);
    return new ResponseEntity(new ErrorResponse().setErrorCode(
        HttpStatus.INTERNAL_SERVER_ERROR.value()).setErrorMessage(e.getMessage())
        , HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public String getFullURL(HttpServletRequest request) {
    StringBuffer requestURL = request.getRequestURL();
    String queryString = request.getQueryString();

    if (StringUtils.isBlank(queryString)) {
      return requestURL.toString();
    } else {
      return requestURL.append('?').append(queryString).toString();
    }
  }
}
