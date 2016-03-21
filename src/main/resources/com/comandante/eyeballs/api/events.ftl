<#-- @ftlvariable name="" type="com.comandante.eyeballs.api.EventsView" -->
<html>
    <body>
        <h1>Motion Events</h1>
     <#list events as item>
     <a href="http://localhost:4444/event/${item.id}">${item.timestamp?datetime}</a><br>
     </#list>
    </body>
</html>