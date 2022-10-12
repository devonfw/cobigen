package com.devonfw.cobigen.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;

import org.junit.Test;
import org.sonatype.plexus.components.sec.dispatcher.model.SettingsSecurity;

import com.devonfw.cobigen.api.util.MavenSettingsUtil;

public class MavenSettingsUtilTest {

  /** Testdata root path */
  private static final String testdataRoot = "src/test/resources/testdata/unittest/MavenSettingsUtilTest";

  /**
   * Tests a simple read of the master password inside the settings security file
   *
   * @throws Exception
   */
  @Test
  public void testReadSettingsSecurityMasterPasswordFromSecuritiesFile() throws Exception {

    String securitiesFile = Paths.get(testdataRoot).resolve("settings-security.xml").toAbsolutePath().toString();
    SettingsSecurity settingsSecurity = MavenSettingsUtil.readSettingsSecurity(securitiesFile);
    assertThat(settingsSecurity.getMaster()).isEqualTo("{/1SNYthinCEHRwQkYnzyoqKp3rbjGyZg/LbW7tOFTEg=}");
  }

  /**
   * Tests the decryption process of the settings security master password
   *
   * @throws Exception
   */
  @Test
  public void testReadSettingsSecurityMasterPassword() throws Exception {

    String decodedMasterPassword = MavenSettingsUtil
        .decryptMasterPassword("{/1SNYthinCEHRwQkYnzyoqKp3rbjGyZg/LbW7tOFTEg=}");
    assertThat(decodedMasterPassword).isEqualTo("testpassword");
  }

  /**
   * Tests the decryption of a password using the decrypted master password as key
   *
   * @throws Exception
   */
  @Test
  public void testReadSettingsPassword() throws Exception {

    String decodedMasterPassword = MavenSettingsUtil
        .decryptMasterPassword("{/1SNYthinCEHRwQkYnzyoqKp3rbjGyZg/LbW7tOFTEg=}");
    String decodedPassword = MavenSettingsUtil.decryptPassword("{jrdiSuRPexMHvBd66Hsiy3nR1VpCc8TDREVCj+6AYrU=}",
        decodedMasterPassword);
    assertThat(decodedPassword).isEqualTo("thisisapassword");
  }
}
