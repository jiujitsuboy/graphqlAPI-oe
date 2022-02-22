package com.openenglish.hr.common.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TokenDecodedInfo implements Serializable {
  private static final long serialVersionUID = -3551443645957159752L;
  private Long personId;
  private String contactId;
  private List<Long> roleIds;
  private List<String> scope;
  private Date issuedDate;
  private Date expiredDate;
}
