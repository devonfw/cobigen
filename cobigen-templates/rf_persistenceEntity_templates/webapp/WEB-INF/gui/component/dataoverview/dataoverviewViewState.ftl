<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE composition PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- @generated -->
<ui:composition xmlns="http://www.w3.org/1999/xhtml"
	xmlns:ui="http://java.sun.com/jsf/facelets"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:sf="http://www.springframework.org/tags/faces"
	xmlns:t="http://myfaces.apache.org/tomahawk"
	template="/WEB-INF/gui/pliscommon/layouts/template.xhtml">

	<ui:define name="title">
		<title><h:outputText
				value="${r"#{msg.MEL_"}${pojo.name}_Overview_Titleline}" /></title>
	</ui:define>

	<ui:define name="content1">
		<ui:include
			src="/WEB-INF/gui/${variables.component}/${pojo.name?lower_case}overview/${pojo.name?lower_case}overviewOverview.xhtml" />
	</ui:define>

	<ui:define name="content2">
		<ui:include
			src="/WEB-INF/gui/${variables.component}/${pojo.name?lower_case}overview/${pojo.name?lower_case}overviewForm.xhtml" />
	</ui:define>

	<ui:define name="buttonline">
		<ui:include
			src="/WEB-INF/gui/${variables.component}/${pojo.name?lower_case}overview/${pojo.name?lower_case}overviewButtonline.xhtml" />
	</ui:define>

	<ui:define name="backline">
		<ui:include
			src="/WEB-INF/gui/pliscommon/pageElements/backButtonLine.xhtml" />
	</ui:define>

</ui:composition>
