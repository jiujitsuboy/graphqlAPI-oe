package com.openenglish.hr.persistence.repository;

import com.openenglish.hr.persistence.entity.aggregation.ActivitiesOverview;

import java.util.List;

public interface CustomActivityRepository {
      List<ActivitiesOverview> getActivitiesOverview(String salesforcePurchaserId);
}
