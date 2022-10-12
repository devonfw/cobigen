package com.devonfw.cobigen.api.util;

import org.sonatype.plexus.components.cipher.DefaultPlexusCipher;
import org.sonatype.plexus.components.cipher.PlexusCipherException;
import org.sonatype.plexus.components.sec.dispatcher.DefaultSecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;
import org.sonatype.plexus.components.sec.dispatcher.SecUtil;
import org.sonatype.plexus.components.sec.dispatcher.model.SettingsSecurity;

public class MavenSettingsUtil {

  /**
   * Reads the settings security file and returns a {@link SettingsSecurity}
   *
   * @param settingsSecurityFile absolute file path to the settings security file
   * @return {@link SettingsSecurity}
   * @throws SecDispatcherException if an error occurred while reading the settings security file
   */
  public static SettingsSecurity readSettingsSecurity(String settingsSecurityFile) throws SecDispatcherException {

    return SecUtil.read(settingsSecurityFile, true);
  }

  /**
   * Decrypts the encrypted password using the decrypted plain text master key
   *
   * @param encryptedPassword String of the encrypted password
   * @param decryptedMasterKey String of the master key
   * @return String of the decrypted password
   * @throws PlexusCipherException if an error occurred during decryption
   */
  public static String decryptPassword(String encryptedPassword, String decryptedMasterKey)
      throws PlexusCipherException {

    DefaultPlexusCipher plexusCipher = new DefaultPlexusCipher();
    return plexusCipher.decryptDecorated(encryptedPassword, decryptedMasterKey);
  }

  /**
   * Decrypts the encrypted master password and returns the master password as a plain text
   *
   * @param encryptedMasterPassword String of the encrypted master password
   * @return String of the decrypted master password
   * @throws PlexusCipherException
   */
  public static String decryptMasterPassword(String encryptedMasterPassword) throws PlexusCipherException {

    return decryptPassword(encryptedMasterPassword, DefaultSecDispatcher.SYSTEM_PROPERTY_SEC_LOCATION);
  }
}
