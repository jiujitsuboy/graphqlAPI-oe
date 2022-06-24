package com.openenglish.hr.persistence.entity.aggregation;

public interface ContactBelongPurchaserId {
    String getContactId();
    String getSalesforcePurchaserId();
    boolean isMatchSalesforcePurchaserId();
}
