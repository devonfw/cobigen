<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>
{
"buttons": {
  "submit": "Enviar",
  "close": "Cerrar",
  "cancel": "CANCELAR",
  "save": "GUARDAR",
  "search": "BUSCAR",
  "clean": "LIMPIAR",
  "addItem": "Añadir fila",
  "editItem": "Editar fila",
  "deleteItem": "Eliminar fila"
},
  "header": {
    "title": "Aplicacion Angular de ${variables.domain}",
    "error": "Error de acceso"
  },
  "login": {
    "title": "Acceso",
    "username": "Nombre de usuario",
    "password": "Contraseña",
    "errorMsg": "Nombre de usuario o contraseña incorrectos"
  },
  "home": "Inicio",
  "${variables.component}datagrid": {
    "navData": "${variables.etoName}_ES",
    "navDataSub": "Tabla de ${variables.etoName}_ES",
    "title": "Tabla de ${variables.etoName?cap_first}_ES",
    "addtitle": "Añadir nueva fila",
    "editTitle": "Editar fila existente",
    "searchTip": "Panel de búsqueda",
    "sortTip": "Limpiar Ordenación",
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
      "title": "Confirmación",
      "message": "¿Seguro que deseas eliminar esta fila?",
      "cancelBtn": "No, Cancelar",
      "acceptBtn": "Sí, Eliminar"
    }
  }
}
