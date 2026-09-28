<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>HTTP Request</title>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/atom-one-dark.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/json.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/bash.min.js"></script>
    <style>
        :root {
            --bg: #1e1f22; --panel: #26272b; --border: #34353a;
            --text: #d7d8dc; --muted: #8b8d93; --accent: #4f8cff;
            --get: #4caf50; --post: #2f9e44; --put: #f08c00; --patch: #e8590c;
            --delete: #e03131; --default: #495057;
        }
        * { box-sizing: border-box; }
        body {
            margin: 0; padding: 16px; background: var(--bg); color: var(--text);
            font-family: -apple-system, "Segoe UI", Roboto, Arial, sans-serif;
            font-size: 13px;
        }
        code, pre { font-family: "SFMono-Regular", Consolas, Menlo, monospace; }
        .head {
            display: flex; align-items: center; gap: 10px; flex-wrap: wrap;
            padding: 10px 12px; background: var(--panel); border: 1px solid var(--border);
            border-radius: 8px; margin-bottom: 14px;
        }
        .method {
            padding: 3px 10px; border-radius: 5px; font-weight: 700; color: #fff;
            letter-spacing: .03em; font-size: 12px;
        }
        .m-GET { background: var(--get); } .m-POST { background: var(--post); }
        .m-PUT { background: var(--put); } .m-PATCH { background: var(--patch); }
        .m-DELETE { background: var(--delete); } .m-OTHER { background: var(--default); }
        .url { word-break: break-all; color: var(--text); }
        h4 {
            margin: 18px 0 6px; font-size: 12px; text-transform: uppercase;
            letter-spacing: .05em; color: var(--muted); font-weight: 600;
        }
        table { width: 100%; border-collapse: collapse; }
        td {
            padding: 4px 8px; border-bottom: 1px solid var(--border);
            vertical-align: top;
        }
        td.key { color: var(--accent); white-space: nowrap; width: 1%; padding-right: 16px; }
        td.val { word-break: break-all; }
        pre {
            margin: 0; padding: 10px 12px; background: var(--panel);
            border: 1px solid var(--border); border-radius: 8px;
            overflow-x: auto; white-space: pre-wrap;
        }
    </style>
</head>
<body>

<div class="head">
    <#assign m = (data.method)!"GET">
    <span class="method <#if m == 'GET'>m-GET<#elseif m == 'POST'>m-POST<#elseif m == 'PUT'>m-PUT
        <#elseif m == 'PATCH'>m-PATCH<#elseif m == 'DELETE'>m-DELETE<#else>m-OTHER</#if>">${m}</span>
    <span class="url">${(data.url)!"Unknown"}</span>
</div>

<#if data.curl??>
    <h4>cURL</h4>
    <pre><code class="language-bash">${data.curl}</code></pre>
</#if>

<#if (data.headers)?has_content>
    <h4>Заголовки</h4>
    <table>
        <#list data.headers as name, value>
            <tr><td class="key">${name}</td><td class="val">${value}</td></tr>
        </#list>
    </table>
</#if>

<#if (data.cookies)?has_content>
    <h4>Cookies</h4>
    <table>
        <#list data.cookies as name, value>
            <tr><td class="key">${name}</td><td class="val">${value}</td></tr>
        </#list>
    </table>
</#if>

<#if data.body??>
    <h4>Тело запроса</h4>
    <pre><code class="language-json" id="req-body">${data.body}</code></pre>
</#if>

<script>
    document.querySelectorAll('#req-body').forEach(function (block) {
        try {
            block.textContent = JSON.stringify(JSON.parse(block.textContent), null, 2);
        } catch (e) { /* тело не JSON — оставляем как есть */ }
    });
    if (window.hljs) { hljs.highlightAll(); }
</script>
</body>
</html>
