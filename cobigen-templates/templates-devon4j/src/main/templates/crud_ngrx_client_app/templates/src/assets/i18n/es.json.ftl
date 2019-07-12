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
    "title": "devon4ng",
    "error": "Error de acceso",
    "EN": "Inglés",
    "ES": "Español"
  },
  "login": {
    "title": "Acceso",
    "username": "Nombre de usuario",
    "password": "Contraseña",
    "errorMsg": "Nombre de usuario o contraseña incorrectos"
  },
  "home": "Inicio",
  "description": "Descripción",
  "example": "Ejemplo",
  "example description": "Descripción de Ejemplo",
  "LIKE": "ME GUSTA",
  "SHARE": "COMPARTIR",
  "CLOSE": "CERRAR",
  "ERROR": "ERROR",
  "${variables.component?lower_case}": {
    "title": "${variables.etoName?cap_first}Data_ES_Tabla",
    "subtitle": "${variables.etoName?cap_first}Data_ES_Descripción",
    "addtitle": "Añadir nueva fila",
    "editTitle": "Editar fila existente",
    "searchTip": "Panel de búsqueda",
    "sortTip": "Limpiar Ordenación",
    "alert": {
      "acceptBtn": "Sí, Eliminar",
      "cancelBtn": "No, Cancelar",
      "title": "Confirmación",
      "message": "¿Seguro que deseas eliminar esta fila?"
    },
    "${variables.etoName?cap_first}": {
      "title": "${variables.etoName?cap_first}_ES_Grid",
      "subtitle": "${variables.etoName?cap_first}_ES_Descripción",
      "navData": "${variables.etoName?cap_first}_ES",
      "navDataSub": "${variables.etoName?cap_first}_ES",
      "navDataSubDescription": "${variables.etoName?cap_first}_EN_Descripción",          
      "columns": {
      <#list pojo.fields as field>
        "${field.name?lower_case}": "${field.name?cap_first}_ES"<#if field?has_next>,</#if>
      </#list>
      }
    }
  }
}
