<!-- @generated -->
<div class="onecol" xmlns:ui="http://java.sun.com/jsf/facelets"
  xmlns:h="http://java.sun.com/jsf/html"
  xmlns:f="http://java.sun.com/jsf/core"
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  xmlns:t="http://myfaces.apache.org/tomahawk">

  <div class="form_wide">

    <div class="form_line">
      <div class="col left">

        <!-- Angebotname -->
        <div class="form_field">

        <#list pojo.fields as attr>

          <t:outputLabel for="${attr.name}" value="${r"#{msg.MEL_"}${pojo.name}_${attr.name?cap_first}}" />
          <t:message for="${attr.name}" showDetail="false"
            errorClass="fehler_icon_validierung" />
          <h:inputText id="${attr.name}" forceId="true"
            value="${r"#{"}manage${pojo.name}Model.getSelected${pojo.name}().${attr.name}}" styleClass="textfield_full" />

        </#list>

        </div>
      </div>
    </div>
  </div>

</div>

