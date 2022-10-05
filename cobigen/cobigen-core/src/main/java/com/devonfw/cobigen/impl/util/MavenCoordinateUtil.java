package com.devonfw.cobigen.impl.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.constants.TemplatesJarConstants;
import com.devonfw.cobigen.api.util.MavenCoordinate;

/**
 * util class
 *
 */
public class MavenCoordinateUtil {

  private static final Logger LOG = LoggerFactory.getLogger(MavenCoordinateUtil.class);

  /**
   * Takes a string with multiple maven coordinates separates them and checks if they meet the maven naming conventions
   * and are therefore valid.
   *
   * @param mavenCoordinatesString a String that contains maven coordinates
   * @return List with {@link MavenCoordinate}
   *
   */
  public static List<MavenCoordinate> convertToMavenCoordinates(List<String> mavenCoordinatesString) {

    List<MavenCoordinate> result = new ArrayList<>();
    for (String mavenCoordinate : mavenCoordinatesString) {
      mavenCoordinate = mavenCoordinate.trim();
      if (!mavenCoordinate.matches(TemplatesJarConstants.MAVEN_COORDINATES_CHECK)) {
        LOG.warn("configuration key:" + mavenCoordinate + " in .cobigen for "
            + "template-sets.installed or template-sets.hide doesnt match the specification and could not be used");
      } else {
        String[] split = mavenCoordinate.split(":");
        String groupID = split[0];
        String artifactID = split[1];
        String version = split.length > 2 ? split[2] : null;
        result.add(new MavenCoordinate(groupID, artifactID, version));
      }
    }
    return result;
  }

}
