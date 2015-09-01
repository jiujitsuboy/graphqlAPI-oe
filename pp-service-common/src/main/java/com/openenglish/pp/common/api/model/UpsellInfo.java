package com.openenglish.pp.common.api.model;

import com.google.common.base.Preconditions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang3.StringUtils;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@AutoProperty
public class UpsellInfo implements Serializable {

  private static final long serialVersionUID = -3551443645957159753L;
  private String accountId;
  private String productId;
  private String productRatePlanChargeId;

  @JsonCreator
  public UpsellInfo(@JsonProperty("accountId")String accountId, @JsonProperty("productId")String productId, @JsonProperty("productRatePlanChargeId")String productRatePlanChargeId) {
    this.setAccountId(accountId);
    this.setProductId(productId);
    this.setProductRatePlanChargeId(productRatePlanChargeId);
  }

  public UpsellInfo() {
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(accountId), "accountId can not be empty!");
    this.accountId = accountId;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(productId), "productId can not be empty!");
    this.productId = productId;
  }

  public String getProductRatePlanChargeId() {
    return productRatePlanChargeId;
  }

  public void setProductRatePlanChargeId(String productRatePlanChargeId) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(productRatePlanChargeId),
                                "productRatePlanChargeId can not be empty!");
    this.productRatePlanChargeId = productRatePlanChargeId;
  }

  @Override
  public boolean equals(Object o) {
    return Pojomatic.equals(this, o);
  }

  @Override
  public int hashCode() {
    return Pojomatic.hashCode(this);
  }

  @Override
  public String toString() {
    return Pojomatic.toString(this);
  }
}
