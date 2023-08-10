package com.devonfw.cobigen.impl.config.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.devonfw.cobigen.api.exception.InvalidConfigurationException;
import com.devonfw.cobigen.api.util.ExceptionUtil;
import com.devonfw.cobigen.api.util.JvmUtil;
import com.devonfw.cobigen.impl.config.constant.ConfigurationVersionEnum;
import com.devonfw.cobigen.impl.config.constant.MavenMetadata;
import com.devonfw.cobigen.impl.config.versioning.VersionValidator;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;

public abstract class JaxbDeserializer {

  /**
   * Reads the templates configuration.
   */
  <R, E extends ConfigurationVersionEnum> R deserialize(Path file, Class<R> deserializeTo, Class<E> versionEnum,
      String rootNodeName) {

    // workaround to make JAXB work in OSGi context by
    // https://github.com/ControlSystemStudio/cs-studio/issues/2530#issuecomment-450991188
    final ClassLoader orig = Thread.currentThread().getContextClassLoader();
    if (JvmUtil.isRunningJava9OrLater()) {
      Thread.currentThread().setContextClassLoader(JAXBContext.class.getClassLoader());
    }

    try (InputStream in = Files.newInputStream(file)) {
      Unmarshaller unmarschaller = JAXBContext.newInstance(deserializeTo).createUnmarshaller();

      // Unmarshal without schema checks for getting the version attribute of the root node.
      // This is necessary to provide an automatic upgrade client later on
      Object rootNode = unmarschaller.unmarshal(in);
      if (deserializeTo.isInstance(rootNode)) {
        BigDecimal configVersion = (BigDecimal) rootNode.getClass().getDeclaredMethod("getVersion").invoke(rootNode);
        if (configVersion == null) {
          throw new InvalidConfigurationException(file.toUri().toString(),
              "The required 'version' attribute of node \"" + rootNodeName + "\" has not been set");
        } else {
          VersionValidator validator = new VersionValidator(VersionValidator.Type.TEMPLATES_CONFIGURATION,
              MavenMetadata.VERSION);
          validator.validate(configVersion.floatValue());
        }
      } else {
        throw new InvalidConfigurationException(file.toUri().toString(),
            "Unknown Root Node. Use \"" + rootNodeName + "\" as root Node");
      }

      // If we reach this point, the configuration version and root node has been validated.
      // Unmarshal with schema checks for checking the correctness and give the user more hints to
      // correct his failures
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      E latestConfigurationVersion = versionEnum.cast(versionEnum.getDeclaredMethod("getLatest").invoke(null));
      try (
          InputStream schemaStream = getClass()
              .getResourceAsStream("/schema/" + latestConfigurationVersion + "/" + rootNodeName + ".xsd");
          InputStream configInputStream = Files.newInputStream(file)) {

        Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
        unmarschaller.setSchema(schema);
        rootNode = unmarschaller.unmarshal(configInputStream);
        return deserializeTo.cast(rootNode);
      }
    } catch (JAXBException e) {
      // try getting SAXParseException for better error handling and user support
      Throwable parseCause = ExceptionUtil.getCause(e, SAXParseException.class, UnmarshalException.class);
      String message = "";
      if (parseCause != null && parseCause.getMessage() != null) {
        message = parseCause.getMessage();
      }
      throw new InvalidConfigurationException(file.toUri().toString(),
          "Could not parse configuration file:\n" + message, e);
    } catch (SAXException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      // Should never occur. Programming error.
      throw new IllegalStateException("Could not parse configuration schema. Please state this as a bug.");
    } catch (NumberFormatException e) {
      // The version number is currently the only xml value which will be parsed to a number data type
      // So provide help
      throw new InvalidConfigurationException(file.toUri().toString(),
          "Invalid version number defined. The version of the configuration should consist of 'major.minor' version.",
          e);
    } catch (IOException e) {
      throw new InvalidConfigurationException(file.toUri().toString(), "Could not read configuration file.", e);
    } finally {
      Thread.currentThread().setContextClassLoader(orig);
    }
  }
}
