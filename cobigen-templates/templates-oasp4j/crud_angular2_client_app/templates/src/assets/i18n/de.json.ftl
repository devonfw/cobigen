{
"buttons": {
    "submit": "Senden",
    "search": "SUCHE",
    "cancel": "STORNIEREN",
    "save": "BEIBEHALTEN",
    "clean": "CLEAR",
    "close": "Schließen",
    "addItem": "Artikel hinzufügen",
    "editItem": "Element bearbeiten",
    "deleteItem": "Element löschen"
  },
  "header": {
    "title": "${variables.domain} angular2 Anwendung",
    "error": "LOGIN FEHLER"
  },
  "login": {
    "title": "Login",
    "username": "Benutzername",
    "password": "Passwort",
    "errorMsg": "Benutzername oder Passwort falsch"
  },
  "${variables.etoName}DataGrid": {
    "navData": "${variables.etoName}_DE",
    "navDataSub": "Raster von ${variables.etoName}_DE",
    "navHome": "Beginn",
    "navHomeSub": "Anfangsseite",
    "title": "${variables.etoName?cap_first}_DE Raster",
    "addTitle": "Füge neuen Gegenstand hinzu",
    "editTitle": "Element bearbeiten",
    "searchTip": "Suchen",
    "sortTip": "Sortierung löschen",
    "cobigen_columns": {
      <#list pojo.fields as field>
        <#if field?has_next>
      "${field.name}": "${field.name?cap_first}_DE",
        <#else>
      "${field.name}": "${field.name?cap_first}_DE"
        </#if>
      </#list>
    },
    "alert": {
      "title": "Bestätigen",
      "message": "Sind Sie sicher, dass Sie dieses Einzelteil löschen möchten?",
      "cancelBtn": "Nein, Abbrechen",
      "acceptBtn": "Ja, Löschen"
    }
  }
}
