<#-- @ftlvariable name="" type="com.comandante.eyeballs.api.EventsView" -->
<html>
    <body>
        <!-- calls getPerson().getName() and sanitizes it -->
        <h1>Hello, ${person.name?html}!</h1>

        [#list getRecentEvents?sort as local_event]
             options = options + "<option  name='${item?string}' value='${item?string}' [#if paymentMethods.contains(item)]selected='selected'[/#if]>${item?string}</option>";
        [/#list]
    </body>
</html>