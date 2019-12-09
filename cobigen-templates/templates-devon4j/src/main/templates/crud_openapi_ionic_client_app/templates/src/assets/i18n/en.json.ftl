{
  "buttons": {
    "login": "Send",
    "logout": "logout",
    "menu": "Menu",
    "back": "back",
    "Add": "Add",
    "Modify": "Modify",
    "Inspect": "Inspect",
    "Delete": "Delete",
    "Filter":"Filter"
  },
  "header": {
    "title": "Ionic",
    "error": "LOGIN ERROR"
  },
   "alert":{
    "title":"Error",
    "subtitle":"Wrong User and/or password",
    "dismiss":"Close"
  },
  "${variables.component?uncap_first}": {
    "title": "${variables.component} Title",
    "description": "This is the basic template for ionic",
    "${variables.etoName?lower_case}": {
     
    <#list model.properties as property>
    "${property.name}":"${property.name}_EN",
    </#list>
    "commonbuttons":{
      "send": "Send",
      "dismiss":"Close"
    },
    "operations": {
       "filter":{
        "title":"Search form",
        "message":"Use this form to search for a item",
        "clear": "Clear Filter"
      }, 
       "add":{
        "title":"Add form",
       "message":"This form is used to add new items"
      },
       "modify":{
        "title":"Modify form",
       "message":"Here you can modify the selected user"
      },
      "delete":{
        "title":"Delete Confirmation Alert",
        "message":"Do you want to delete the selected element?",
        "dismiss":"No, cancel",
        "confirm":"Yes"
      }
    }
    }
  },
  "login": {
    "user": "User",
    "username": "Username",
    "password": "Password",
    "errorMsg": "Wrong username or password"
  },
  "example":"example",
  "Home":"Home"
  }