<#-- @ftlvariable name="" type="com.comandante.eyeballs.api.EventsView" -->
<html>
    <body>
        <h1>Recent events..</h1>
     <#list events as item>
     <a href="http://localhost:4444/event/${item}">${item}</a><br>
     </#list>
    </body>
</html>