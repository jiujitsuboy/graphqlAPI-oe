package com.openenglish.hr.persistence.entity.aggregation;

public interface EmailBelongPurchaserId {
    String getContactId();
    String getEmail();
    String getSalesForcePurchaserId();
    boolean isMatchSalesForcePurchaserId();
}
