<#include '/makros.ftl'>
package ${variables.rootPackage}.common.builders<#if variables.subPackage != "null">.${variables.subPackage}</#if>;

import java.util.LinkedList;
import java.util.List;

import ${pojo.package}.${pojo.name};
import ${variables.rootPackage}.common.builders.P;

public class ${pojo.name}Builder {

	/**
	 *  Might be enrichted to users needs (will not be overwritten)
	 */
    private void fillMandatoryFields_custom() {

    }

}