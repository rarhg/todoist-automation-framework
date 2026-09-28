<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpResponseAttachment" -->
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>HTTP Response</title>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/atom-one-dark.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/json.min.js"></script>
    <style>
        :root {
            --bg: #1e1f22; --panel: #26272b; --border: #34353a;
            --text: #d7d8dc; --muted: #8b8d93; --accent: #4f8cff;
            --ok: #2f9e44; --redirect: #1c7ed6; --client-err: #f08c00;
            --server-err: #e03131; --default: #495057;
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
        .status {
            padding: 3px 10px; border-radius: 5px; font-weight: 700; color: #fff;
            letter-spacing: .03em; font-size: 12px;
        }
        .s-2xx { background: var(--ok); } .s-3xx { background: var(--redirect); }
        .s-4xx { background: var(--client-err); } .s-5xx { background: var(--server-err); }
        .s-default { background: var(--default); }
        .url { word-break: break-all; color: var(--muted); font-size: 12px; }
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
    <#if (data.responseCode)??>
        <#assign code = data.responseCode>
        <span class="status
            <#if code gte 200 && code lt 300>s-2xx
            <#elseif code gte 300 && code lt 400>s-3xx
            <#elseif code gte 400 && code lt 500>s-4xx
            <#elseif code gte 500>s-5xx
            <#else>s-default</#if>">${code?c}</span>
    <#else>
        <span class="status s-default">Unknown</span>
    </#if>
    <#if data.url??><span class="url">${data.url}</span></#if>
</div>

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
    <h4>Тело ответа</h4>
    <pre><code class="language-json" id="resp-body">${data.body}</code></pre>
</#if>

<script>
    document.querySelectorAll('#resp-body').forEach(function (block) {
        try {
            block.textContent = JSON.stringify(JSON.parse(block.textContent), null, 2);
        } catch (e) { /* тело не JSON — оставляем как есть */ }
    });
    if (window.hljs) { hljs.highlightAll(); }
</script>
</body>
</html>
