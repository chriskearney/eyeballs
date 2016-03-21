<#-- @ftlvariable name="" type="com.comandante.eyeballs.api.EventsView" -->
<html>
    <body>
        <h2>Motion Events</h2>
     <#list events as item>
     <a href="${baseUrl}/${item.id}">${item.timestamp?datetime}</a><br>
     </#list>
    </body>
</html>