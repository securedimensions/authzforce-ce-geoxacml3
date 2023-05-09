<!-- HTML for static distribution bundle build -->
<!DOCTYPE html>
<html lang="en">
<head>
    <#include "_head.html">
</head>

<body>
<#include "_menue.html">
<div class="content">
    <div class="container" id="content">
        <div id="swagger-ui"></div>
    </div>
</div>

<script src="pdp/static/js/swagger-ui-bundle.js"></script>
<script src="pdp/static/js/swagger-ui-standalone-preset.js"></script>
<script>
    window.onload = function () {

        // Build a system
        const ui = SwaggerUIBundle({
            url: "${API_JSON_URL}",
            dom_id: '#swagger-ui',
            deepLinking: true,
            presets: [
                SwaggerUIBundle.presets.apis,
                SwaggerUIStandalonePreset
            ],
            plugins: [
                SwaggerUIBundle.plugins.DownloadUrl
            ],
            layout: "StandaloneLayout"
        })

        window.ui = ui
    }
</script>

<footer class="footer">
    <#include "_footer.html">
</footer>

</body>
