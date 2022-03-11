package com.devonfw.cobigen.openapiplugin.matcher;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.cobigen.api.exception.CobiGenRuntimeException;
import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.extension.MatcherInterpreter;
import com.devonfw.cobigen.api.to.GenerationReportTo;
import com.devonfw.cobigen.api.to.MatcherTo;
import com.devonfw.cobigen.api.to.VariableAssignmentTo;
import com.devonfw.cobigen.openapiplugin.model.EntityDef;
import com.devonfw.cobigen.openapiplugin.util.constants.Constants;

/**
 * Matcher for internal OpenAPI model.
 */
public class OpenAPIMatcher implements MatcherInterpreter {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(OpenAPIMatcher.class);

  /** Supported matcher types */
  private enum MatcherType {
    /** Element type */
    ELEMENT,
    /** Entity name */
    ENTITYNAMEREGEX
  }

  /** Matcher variable types */
  private enum VariableType {
    /** Constant assignment */
    CONSTANT,
    /** Object property extraction */
    PROPERTY,
    /**
     * Extension property extraction of the info extensions and the schema extensions of the entities
     */
    EXTENSION
  }

  @Override
  public boolean matches(MatcherTo matcher) {

    try {
      MatcherType matcherType = Enum.valueOf(MatcherType.class, matcher.getType().toUpperCase());
      switch (matcherType) {
        case ELEMENT:
          // to lower case to prevent from simple error cases
          return matcher.getTarget().getClass().getSimpleName().toLowerCase().equals(matcher.getValue().toLowerCase());
        case ENTITYNAMEREGEX:
          Object target = matcher.getTarget();
          if (target.getClass().getSimpleName().toLowerCase().equals(EntityDef.class.getSimpleName().toLowerCase())) {
            Pattern p = Pattern.compile(matcher.getValue());
            Matcher m = p.matcher(((EntityDef) target).getName());
            if (m.matches()) {
              return true;
            }
          }
          return false;
        default:
          break;
      }
    } catch (IllegalArgumentException e) {
      LOG.info("Matcher type '{}' not registered --> no match!", matcher.getType());
    }

    return false;
  }

  @Override
  public Map<String, String> resolveVariables(MatcherTo matcher, List<VariableAssignmentTo> variableAssignments,
      GenerationReportTo report) throws InvalidConfigurationException {

    Map<String, String> resolvedVariables = new HashMap<>();
    VariableType variableType = null;
    for (VariableAssignmentTo va : variableAssignments) {
      try {
        variableType = Enum.valueOf(VariableType.class, va.getType().toUpperCase());
      } catch (InvalidConfigurationException e) {
        throw new CobiGenRuntimeException(
            "Matcher or VariableAssignment type " + matcher.getType() + " not registered!", e);
      }
      switch (variableType) {
        case CONSTANT:
          resolvedVariables.put(va.getVarName(), va.getValue());
          break;
        case EXTENSION:
          Class<?> targetObject = matcher.getTarget().getClass();
          try {
            Field field = targetObject.getDeclaredField("extensionProperties");
            field.setAccessible(true);
            Object extensionProperties = field.get(matcher.getTarget());

            String attributeValue = getExtendedProperty((Map<String, Object>) extensionProperties, va.getValue());

            resolvedVariables.put(va.getVarName(), attributeValue);
          } catch (NoSuchFieldException | SecurityException e) {
            if (va.isMandatory()) {
              String errorMessage = Constants.getMandatoryMessage(true, va.getValue());
              report.addError(new CobiGenRuntimeException(errorMessage));
              LOG.error(errorMessage);
            } else {
              String warningMessage = Constants.getMandatoryMessage(false, va.getValue());
              report.addWarning(warningMessage);
              resolvedVariables.put(va.getVarName(), "");
              LOG.warn(warningMessage);
            }

          } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new CobiGenRuntimeException("This is a programming error, please report an issue on github", e);
          }
          break;
        case PROPERTY:
          Class<?> target = matcher.getTarget().getClass();
          try {
            Field field = target.getDeclaredField(va.getValue());
            field.setAccessible(true);
            Object o = field.get(matcher.getTarget());

            resolvedVariables.put(va.getVarName(), o.toString());
          } catch (NoSuchFieldException | SecurityException e) {
            LOG.warn(
                "The property {} was requested in a variable assignment although the input does not provide this property. Setting it to empty",
                matcher.getValue());
          } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new CobiGenRuntimeException("This is a programming error, please report an issue on github", e);
          }
          break;
      }

    }
    return resolvedVariables;
  }

  /**
   * Tries to cast the object "extensionProperties" to a map like the one defined in
   * {@link com.devonfw.cobigen.openapiplugin.model.EntityDef} . <br>
   * <br>
   * Additionally, tries to get a value of that map using the key passed as parameter.
   *
   * @param object to be cast to a Map
   * @param key to search in the Map
   * @return the value of that key, and if nothing was found, an empty string
   * @throws NoSuchFieldException if the property isn't given
   */
  private String getExtendedProperty(Map<String, Object> extensionProperties, String key) throws NoSuchFieldException {

    Map<String, Object> properties = extensionProperties;
    if (properties.containsKey(key)) {
      return properties.get(key).toString();
    } else {
      throw new NoSuchFieldException("couldn't find property: " + key);
    }
  }

}
