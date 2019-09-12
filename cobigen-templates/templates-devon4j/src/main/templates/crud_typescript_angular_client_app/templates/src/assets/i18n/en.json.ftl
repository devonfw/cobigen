<#import '/variables.ftl' as class>
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
    "title": "devon4ng",
    "error": "LOGIN ERROR",
    "EN": "English",
    "ES": "Spanish"
  },
  "login": {
    "title": "Login",
    "username": "Username",
    "password": "Password",
    "errorMsg": "Wrong username or password"
  },
  "home": "Home",
  "description": "Description",
  "example": "Example",
  "example description": "Example Description",
  "LIKE": "LIKE",
  "SHARE": "SHARE",
  "CLOSE": "CLOSE",
  "ERROR": "ERROR",    
  "${variables.component?lower_case}": {
    "addTitle": "Add new item",        
    "editTitle": "Edit item",
    "searchTip": "Search Panel",
    "sortTip": "Clear Sorting",
    "alert": {
      "acceptBtn": "Yes, Delete",
      "cancelBtn": "No, Cancel",
      "title": "Confirm",
      "message": "Are you sure you want to delete this item?"
    },
    "${variables.entityName?cap_first}": {
      "title": "${variables.entityName?cap_first}_EN_Grid",
      "subtitle": "${variables.entityName?cap_first}_EN_Description",
      "navData": "${variables.entityName?cap_first}_EN",
      "navDataSub": "${variables.entityName?cap_first}_EN",
      "navDataSubDescription": "${variables.entityName?cap_first}_EN_Description",
      "columns": {
      <#list class.properties as property>
        "${property.identifier?lower_case}": "${property.identifier?cap_first}_EN"<#if property?has_next>,</#if>
      </#list>
      }
    }
  }
}
