{
  "buttons": {
    "cancel": "CANCELAR",
    "addItem": "Añadir fila",
    "search": "BUSCAR",
    "submit": "Enviar",
    "editItem": "Editar fila",
    "deleteItem": "Eliminar fila",
    "save": "GUARDAR",
    "clean": "LIMPIAR",
    "close": "Cerrar"
  },
  "LIKE": "ME GUSTA",
  "${variables.component?lower_case}": {
    "alert": {
      "acceptBtn": "Sí, Eliminar",
      "cancelBtn": "No, Cancelar",
      "title": "Confirmación",
      "message": "¿Seguro que deseas eliminar esta fila?"
    },
    "${variables.etoName?cap_first}": {
      "navDataSubDescription": "${variables.etoName?cap_first}_ES_Descripción",
      "navDataSub": "${variables.etoName?cap_first}_ES",
      "columns": {
      <#list model.properties as property>
        "${property.name?uncap_first}": "${property.name?cap_first}_ES"<#if property?has_next>,</#if>
      </#list>
      },
      "subtitle": "${variables.etoName?cap_first}_ES_Descripción",
      "title": "${variables.etoName?cap_first}_ES_Grid",
      "navData": "${variables.etoName?cap_first}_ES"
    },
    "subtitle": "${variables.etoName?cap_first}_ES_Descripción",
    "editTitle": "Editar fila existente",
    "title": "${variables.etoName?cap_first}_ES_Tabla",
    "addtitle": "Añadir nueva fila",
    "searchTip": "Panel de búsqueda",
    "sortTip": "Limpiar Ordenación"
  },
  "example description": "Descripción de Ejemplo",
  "header": {
    "EN": "Inglés",
    "title": "devon4ng",
    "error": "Error de acceso",
    "ES": "Español"
  },
  "description": "Descripción",
  "ERROR": "ERROR",
  "CLOSE": "CERRAR",
  "login": {
    "password": "Contraseña",
    "title": "Acceso",
    "username": "Nombre de usuario",
    "errorMsg": "Nombre de usuario o contraseña incorrectos"
  },
  "SHARE": "COMPARTIR",
  "home": "Inicio",
  "example": "Ejemplo"
}
