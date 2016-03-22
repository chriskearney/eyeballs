<#-- @ftlvariable name="" type="com.comandante.eyeballs.api.EventsView" -->
<html>
    <body>
        <h2>Motion Events</h2>
     <#list events as item>
     <#if displayImage>
          <a href="${baseUrl}/event/${item.id}"><img src="${baseUrl}/event/${item.id}"></a><br>
     <#else>
          <a href="${baseUrl}/event/${item.id}">${item.timestamp?datetime}</a><br>
     </#if>
     </#list>
    </body>
</html>