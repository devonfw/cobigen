<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>
{
"buttons": {
    "submit": "Send",
    "search": "SEARCH",
    "cancel": "CANCEL",
    "save": "SAVE",
    "clean": "CLEAR",
    "close": "Close",
    "addItem": "Add item",
    "editItem": "Edit item",
    "deleteItem": "Delete item"
  },
  "header": {
    "title": "${variables.domain} Angular application",
    "error": "LOGIN ERROR"
  },
  "login": {
    "title": "Login",
    "username": "Username",
    "password": "Password",
    "errorMsg": "Wrong username or password"
  },
  "home": "Home",
  "${variables.component}datagrid": {
    "navData": "${variables.etoName}_EN",
    "navDataSub": "Grid of ${variables.etoName}_EN",
    "title": "${variables.etoName?cap_first}_EN grid",
    "addTitle": "Add new item",
    "editTitle": "Edit item",
    "searchTip": "Search Panel",
    "sortTip": "Clear Sorting",
    "columns": {
      <#list elemDoc["self::node()/ownedAttribute"] as field>
        <#if field?has_next>
      "${field["@name"]}": "${field["@name"]?cap_first}_ES",
        <#else>
      "${field["@name"]}": "${field["@name"]?cap_first}_ES"
        </#if>
      </#list>
    },
    "alert": {
      "title": "Confirm",
      "message": "Are you sure you want to delete this item?",
      "cancelBtn": "No, Cancel",
      "acceptBtn": "Yes, Delete"
    }
  }
}
