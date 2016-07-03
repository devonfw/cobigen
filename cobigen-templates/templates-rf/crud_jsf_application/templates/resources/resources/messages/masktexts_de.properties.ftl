##############################################################################################
# Maskentexte von Web-Austausch-Plattform
##############################################################################################
MEL_Applicationname = !Application name!


##############################################################################################
# MAS_General
##############################################################################################

MEL_Entry_Area_Titleline = Übersicht
MEL_Zur_Uebersicht = Zurück zur Übersicht
MEL_Start = Start
MEL_To_Startpage = Zur Startseite
MEL_Back = Zurück
MEL_Send = Abschicken
MEL_Logout = Logout

MEL_Breadcrumb_prefix = Sie befinden sich hier:
MEL_Startseite = Startseite
MEL_Hilfe = Hilfe

MEL_Notebox = Hinweise:
MEL_Error = Fehler!
MEL_Errorinfo = Ein technischer Fehler ist aufgetreten. Der IT-Betrieb wurde informiert.
MEL_Errorbox = Folgende Fehler sind aufgetreten:
MEL_LoginAs = Sie sind eingeloggt als

MEL_Id = Nummer
MEL_Back = Zurück
MEL_Link = öffnen
MEL_Select = Auswählen
MEL_Edit = Bearbeiten
MEL_Edit_Delete = Bearbeiten/Löschen
MEL_Delete = Löschen
MEL_Save = Speichern
MEL_Create = Erstellen
MEL_Create_New = Neu erstellen
MEL_Name = Name
MEL_Price = Preis
MEL_Min_Price = Minimaler Preis
MEL_Max_Price = Maximaler Preis
MEL_Type = Type
MEL_ALL = Alle
MEL_Filter = Filtern
MEL_Action = Aktion

##############################################################################################
# MAS_${pojo.name}Management
##############################################################################################

MEL_Create_${pojo.name}_Titleline=Create ${pojo.name}
<#list pojo.fields as attr>
MEL_${pojo.name}_${attr.name?cap_first}=${attr.name?cap_first}
</#list>

MEL_${pojo.name}_Overview_Titleline = ${pojo.name} Overview
MEL_Create_${pojo.name}_Titleline = Create ${pojo.name}
MEL_Manage_${pojo.name}_Titleline = Edit ${pojo.name}

MEL_${pojo.name}_Overview = Manage ${pojo.name}s
MEL_Create_${pojo.name} = Create ${pojo.name}
