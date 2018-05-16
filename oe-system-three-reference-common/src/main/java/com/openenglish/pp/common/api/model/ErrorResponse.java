package com.openenglish.pp.common.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ErrorResponse implements Serializable {
  private static final long serialVersionUID = -4612531115829368460L;
  private String errorMessage;
  private Integer errorCode;
}
