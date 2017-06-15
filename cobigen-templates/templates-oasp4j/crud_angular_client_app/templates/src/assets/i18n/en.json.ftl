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
  "${variables.component}datagrid": {
    "navData": "${variables.etoName}_EN",
    "navDataSub": "Grid of ${variables.etoName}_EN",
    "navHome": "Home",
    "navHomeSub": "Initial page",
    "title": "${variables.etoName?cap_first}_EN grid",
    "addTitle": "Add new item",
    "editTitle": "Edit item",
    "searchTip": "Search Panel",
    "sortTip": "Clear Sorting",
    "columns": {
      <#list pojo.fields as field>
        <#if field?has_next>
      "${field.name}": "${field.name?cap_first}_EN",
        <#else>
      "${field.name}": "${field.name?cap_first}_EN"
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
