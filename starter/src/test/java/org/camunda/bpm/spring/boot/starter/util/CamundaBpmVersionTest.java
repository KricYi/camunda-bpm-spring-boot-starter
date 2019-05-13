package org.camunda.bpm.spring.boot.starter.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.env.PropertiesPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.spring.boot.starter.util.CamundaBpmVersion.key;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CamundaBpmVersionTest {

  private static final String CURRENT_VERSION = "7.8.0";

  public static CamundaBpmVersion camundaBpmVersion(final String version) {
    final Package pkg = mock(Package.class);
    when(pkg.getImplementationVersion()).thenReturn(version);
    return new CamundaBpmVersion(pkg);
  }

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void current_version() {
    final CamundaBpmVersion version =  new CamundaBpmVersion();
    assertThat(version.isEnterprise()).isFalse();
    assertThat(version.get()).startsWith(CURRENT_VERSION);

    final PropertiesPropertySource source = version.getPropertiesPropertySource();
    assertThat(source.getName()).isEqualTo("CamundaBpmVersion");
    final String versionFromPropertiesSource = (String) source.getProperty(key(CamundaBpmVersion.VERSION));
    assertThat(versionFromPropertiesSource).startsWith(CURRENT_VERSION);
    assertThat(source.getProperty(key(CamundaBpmVersion.FORMATTED_VERSION))).isEqualTo("(v" + versionFromPropertiesSource + ")");
    assertThat(source.getProperty(key(CamundaBpmVersion.IS_ENTERPRISE))).isEqualTo(Boolean.FALSE);
  }

  @Test
  public void isEnterprise_true() throws Exception {
    assertThat(camundaBpmVersion("7.6.0-alpha3-ee").isEnterprise()).isTrue();
  }

  @Test
  public void isEnterprise_false() throws Exception {
    assertThat(camundaBpmVersion("7.6.0-alpha3").isEnterprise()).isFalse();
  }

  @Test
  public void isLaterThanOrEqual_equal_true() {
    assertThat(camundaBpmVersion("7.9.2-ee").isLaterThanOrEqual("7.9.2")).isTrue();
  }

  @Test
  public void isLaterThanOrEqual_later_true() {
    assertThat(camundaBpmVersion("7.9.2-ee").isLaterThanOrEqual("7.8.3")).isTrue();
  }

  @Test
  public void isLaterThanOrEqual_earlier_false() {
    assertThat(camundaBpmVersion("7.9.2").isLaterThanOrEqual("7.10.5-ee")).isFalse();
  }

  @Test
  public void isLaterThanOrEqual_bad_format() {
    thrown.expect(RuntimeException.class);
    thrown.expectMessage("Exception while checking version numbers '7.9.2' and '7.10'. "
      + "Version numbers are missing or incompatible.");

    camundaBpmVersion("7.9.2").isLaterThanOrEqual("7.10");
  }
}
