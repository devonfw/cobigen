package com.capgemini.cobigen.javaplugin.inputreader;

import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableClassLibraryBuilder;
import com.capgemini.cobigen.javaplugin.merger.libextension.ModifyableJavaClass;
import com.thoughtworks.qdox.library.ClassLibraryBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Abstract test class for Java parser tests, which provides useful functionality for Java parser testing
 * 
 * @author <a href="m_brunnl@cs.uni-kl.de">Malte Brunnlieb</a>
 * @version $Revision$
 */
public abstract class AbstractJavaParserTest {

    /**
     * Returns the first {@link JavaClass} parsed by the given {@link Reader}, all upcoming parsed java files will be
     * added to the class library
     * 
     * @param reader
     *        {@link Reader}s which contents should be parsed
     * @return the parsed {@link JavaClass}
     * @author mbrunnli (19.03.2013)
     */
    protected ModifyableJavaClass getJavaClass(Reader... reader) {

        ClassLibraryBuilder classLibraryBuilder = new ModifyableClassLibraryBuilder();
        classLibraryBuilder.appendDefaultClassLoaders();
        JavaSource source = null;
        ModifyableJavaClass targetClass = null;
        for (Reader r : reader) {
            source = classLibraryBuilder.addSource(r);
            if (targetClass == null)
                targetClass = (ModifyableJavaClass) source.getClasses().get(0);
        }
        return targetClass;
    }

    /**
     * Returns the model root-element ({@link ModelConstant#ROOT})
     * 
     * @param model
     * @return the model root-element ({@link ModelConstant#ROOT})
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> getRoot(Map<String, Object> model) {

        Map<String, Object> pojoMap = (Map<String, Object>) model.get(ModelConstant.ROOT);
        Assert.assertNotNull(ModelConstant.ROOT + " is not accessible in model", pojoMap);
        return pojoMap;
    }

    /**
     * Returns the list of all field models ({@link ModelConstant#FIELDS})
     * 
     * @param model
     *        raw model
     * @return the list of all field models
     */
    @SuppressWarnings("unchecked")
    protected List<Map<String, Object>> getFields(Map<String, Object> model) {

        List<Map<String, Object>> attributes = (List<Map<String, Object>>) getRoot(model).get(ModelConstant.FIELDS);
        Assert.assertNotNull(ModelConstant.FIELDS + " is not accessible in model", attributes);
        return attributes;
    }

    /**
     * Returns the field model with the given field name from model
     * 
     * @param model
     *        raw model
     * @param fieldName
     *        field name to be retrieved
     * @return the field model for the given field name. If the field could not be found, an assertion exception will be
     *         thrown
     */
    protected Map<String, Object> getField(Map<String, Object> model, String fieldName) {

        Map<String, Object> field = null;
        for (Map<String, Object> attr : getFields(model)) {
            if (fieldName.equals(attr.get(ModelConstant.NAME))) {
                field = attr;
                break;
            }
        }

        Assert.assertNotNull("There is no field with name '" + fieldName + "' in the model", field);
        return field;
    }

}
