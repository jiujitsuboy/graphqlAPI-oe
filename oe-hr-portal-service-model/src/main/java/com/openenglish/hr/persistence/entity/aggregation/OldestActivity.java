package com.openenglish.hr.persistence.entity.aggregation;

import java.time.LocalDateTime;

public interface OldestActivity {
  Long getCourseTypeId();
  LocalDateTime getOldestActivityDate();
}
