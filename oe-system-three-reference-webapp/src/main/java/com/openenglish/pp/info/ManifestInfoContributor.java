package com.openenglish.pp.info;

import com.openenglish.substrate.environment.ManifestInfo;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class ManifestInfoContributor implements InfoContributor {

  private final ManifestInfo manifestInfo;

  public ManifestInfoContributor(ManifestInfo manifestInfo) {
    this.manifestInfo = manifestInfo;
  }

  @Override
  public void contribute(Builder builder) {
    builder.withDetail("Build JDK", manifestInfo.getManifestBuildJdk())
        .withDetail("SHA1", manifestInfo.getManifestSha1())
        .withDetail("VersionInfo", manifestInfo.getManifestVersionInfo())
        .withDetail("Classpath", manifestInfo.getManifestClassPath());
  }
}
