<#assign entityRef = "false">

<#list model.paths as path>
  <#list path.operations as operation>
    <#list operation.responses as response>
      <#if response.entityRef??>
        <#assign entityRef = response.entityRef>        
      </#if>      
    </#list>
  </#list>
</#list>

<#if entityRef != "false">
import {${entityRef.name}} from '../model/${entityRef.name?lower_case}Eto';
</#if>

@Injectable()
export class ${variables.component}RestControllerService {
  <#list model.paths as path>
      <#list path.operations as operation>
    <#compress>
      
    /**
    * <#if operation.type??> Operation type: ${operation.type} </#if>
    * <#if operation.description??> Operation description: ${operation.description} </#if>
    * <#if operation.tag??> Operation tag:<#list operation.tags as tag> ${operation.tag} </#list> </#if>
    * <#if operation.responses??> <#list operation.responses as response> <#if response.type??> Response type: ${response.type} </#if>
    <#if response.code??>* Response code: ${response.code} </#if>
    <#if response.description??>* Response description: ${response.description} </#if>
    * <#if response.mediaTypes??> <#list response.mediaTypes as mediaType> Media Type: ${mediaType} </#list> </#if> 
    * <#if response.parameters??> <#list response.parameters as parameter> Parameter: ${parameter.mediaType} </#list> </#if> </#list> </#if>
    **/   
    
    <#if path.version??>  
    /**
    * Path version: ${path.version}
    **/
    </#if>
    </#compress>
    
    
    /**
     * <#if operation.operationId??>${operation.operationId}</#if>
     * 
     * @param query query
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public <#if operation.operationId??>${operation.operationId}</#if>(query?: string, observe?: 'body', reportProgress?: boolean): Observable<<#if entityRef != "false">${entityRef.name}</#if>>{

        return this.httpClient.get<<#if entityRef != "false">${entityRef.name}</#if>>(`${r"${this.basePath}"}/${variables.component?lower_case}<#if operation.summary??>/${operation.summary}/</#if><#if path.pathURI??>${path.pathURI}</#if>${r"${encodeURIComponent(String(query))}"}`;
    }
      </#list>
  </#list>

}
