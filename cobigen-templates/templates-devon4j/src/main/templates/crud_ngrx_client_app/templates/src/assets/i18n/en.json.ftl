
{
  "buttons": {
    "cancel": "CANCEL",
    "addItem": "Add item",
    "search": "SEARCH",
    "submit": "Send",
    "editItem": "Edit item",
    "deleteItem": "Delete item",
    "save": "SAVE",
    "clean": "CLEAR",
    "close": "Close"
  },

  "LIKE": "LIKE",
  "${variables.component?lower_case}": {
    "addTitle": "Add new item",
    "alert": {
      "acceptBtn": "Yes, Delete",
      "cancelBtn": "No, Cancel",
      "title": "Confirm",
      "message": "Are you sure you want to delete this item?"
    },
    "${variables.etoName?cap_first}": {
      "navDataSubDescription": "${variables.etoName?cap_first}_EN_Description",
      "navDataSub": "${variables.etoName?cap_first}_EN",
      "columns": {
       <#list pojo.fields as field>
        "${field.name?uncap_first}": "${field.name?cap_first}_EN"<#if field?has_next>,</#if>
        </#list>
      },
      "subtitle": "${variables.etoName?cap_first}_EN_Description",
      "title": "${variables.etoName?cap_first}_EN_Grid",
      "navData": "${variables.etoName?cap_first}_EN"
    },
    "editTitle": "Edit item",
    "searchTip": "Search Panel",
    "sortTip": "Clear Sorting"
  },
  "example description": "Example Description",
  "header": {
    "EN": "English",
    "title": "devon4ng",
    "error": "LOGIN ERROR",
    "ES": "Spanish"
  },
  "description": "Description",
  "ERROR": "ERROR",
  "CLOSE": "CLOSE",
  "login": {
    "password": "Password",
    "title": "Login",
    "username": "Username",
    "errorMsg": "Wrong username or password"
  },
  "SHARE": "SHARE",
  "home": "Home",
  "example": "Example"
}