# Todoist QA Automation

[![CI](https://github.com/rarhg/todoist-automation-framework/actions/workflows/ci.yml/badge.svg)](https://github.com/rarhg/todoist-automation-framework/actions/workflows/ci.yml)
[![Allure Report](https://img.shields.io/badge/Allure-report-orange)](https://rarhg.github.io/todoist-automation-framework/)

Дипломный проект по автоматизации тестирования [Todoist](https://todoist.com): API, Web, Mobile (Android) и слой БД в одном Gradle-монорепозитории с единым Allure-отчётом.

<!-- Скриншот отчёта: положите файл в docs/ и раскомментируйте
![Allure Report](docs/allure-report.png)
-->

## Стек

Java 17 · Gradle · JUnit 5 · AssertJ · Datafaker · Lombok · Allure 2.27.0 · REST Assured 5.4.0 + WireMock 3.5.4 · Selenide 7.3.0 · Appium java-client 9.2.2 · Testcontainers 1.21.4 (PostgreSQL 16) · OWNER (конфигурация)

## Структура

```
todoist-qa-automation
├── common   — конфигурация: ProjectConfig, ConfigProvider, config/common.properties
├── api      — модели, Specs, Steps, WireMock-стабы (main) и API-тесты (test)
├── web      — Selenide: pages, components, helpers (сессия), тесты
├── mobile   — Appium: screens, helpers, тесты (локальное устройство и BrowserStack)
├── db       — Testcontainers + PostgreSQL: DAO, сервис синхронизации, тест
├── config/allure — categories.json и environment.properties для отчёта
└── docs
```

Зависимости модулей: `api → common`; `web`, `mobile`, `db → common + api` (`api` нужен для подготовки и очистки данных через реальный API).

## Что покрыто

| Слой | Тег | Что проверяется |
|------|-----|-----------------|
| API | `api` | CRUD по проектам, задачам, разделам, меткам и комментариям на WireMock; негативные сценарии; контракт задачи по JSON-схеме |
| Auth API | `api`, `live` | Запросы к реальному API без токена и с невалидным токеном (ожидается 401/403) |
| Web | `web` | Создание, выполнение, удаление и редактирование задачи; создание проекта; навигация между списками |
| Mobile | `android` | Вход, негативная регистрация, выход, создание задачи и NLP-распознавание дат, выполнение задачи, календарь, раскладка Список/Доска |
| DB | `db` | Синхронизация активных и завершённых задач из API в PostgreSQL |

## Быстрый старт

### Требования

- JDK 17
- Docker (для модуля `db`)
- Chrome (для `web`)
- Android-устройство или эмулятор, Appium и APK (для `mobile`, см. ниже)

### Конфигурация

Настройки читаются в порядке от низшего приоритета к высшему:

1. `common/src/main/resources/config/common.properties` — в git: URL, версия API, параметры браузера и Appium, несекретные параметры BrowserStack.
2. `common/src/main/resources/config/local.properties` — **вне git**: токены, логин и пароль, ключи BrowserStack, параметры вашей машины.
3. Системные свойства `-D` в командной строке.

Для нового клона скопируйте шаблон `local.properties.example` в `common/src/main/resources/config/local.properties` и заполните:

```properties
api.token=...          # токен API тестового аккаунта Todoist
test.email=...
test.password=...
# для mobile:
local.device.name=...  # имя устройства/эмулятора (adb devices)
android.sdk.home=...   # путь к Android SDK
# для BrowserStack:
bs.username=...
bs.access.key=...
bs.app.url=...
```

Параметры через командную строку пробрасываются в тестовую JVM, если ключ начинается с `web.`, `api.`, `test.` либо это `browser`, `is.remote`, `remote.url`, `auth.state.file`. Например, чтобы увидеть окно браузера:

```
./gradlew :web:test -Dweb.headless=false
```

По умолчанию `web.headless=true`. Для отладки удобнее поставить `web.headless=false` в `local.properties`.

### Запуск

```
./gradlew :api:test          # API-тесты (моки + live-проверка авторизации)
./gradlew :db:test           # DB-тесты (нужен Docker и api.token)
./gradlew :web:test          # Web-тесты (нужна сохранённая сессия, см. ниже)
./gradlew :mobile:androidLocal   # Mobile на локальном устройстве или эмуляторе
./gradlew :mobile:android        # Mobile в BrowserStack
```

Запуск одного класса: `./gradlew :web:test --tests "*TaskUiTest"`.

### Web: вход по сохранённой сессии

Форма входа Todoist защищена капчей и отклоняет автоматизированный браузер, поэтому web-тесты не логинятся сами. Вход выполняется вручную один раз, а сессия сохраняется в файл `web/auth-state.json` (он в `.gitignore`).

1. Запустите Chrome с отладочным портом и отдельным профилем:
   ```
   chrome.exe --remote-debugging-port=9222 --user-data-dir="C:\chrome-todoist-profile"
   ```
2. В этом окне войдите на app.todoist.com вручную (капчу проходите руками).
3. Выполните `./gradlew :web:saveSession`.

Сессия живёт около двух недель, после этого шаги нужно повторить. В CI содержимое файла передаётся через переменную окружения `AUTH_STATE_JSON`.

### Mobile: что нужно для запуска

- Android-устройство или эмулятор с русской локалью (тесты используют русские тексты интерфейса).
- Установленный Appium с драйвером UiAutomator2 и Android SDK (путь в `android.sdk.home`).
- APK Todoist. В репозитории его нет намеренно (проприетарный бинарник стороннего
  приложения, раздувает историю git и не нужен для сборки/CI). Получить его можно
  с собственного устройства, где приложение уже установлено через Google Play:

  ```
  adb shell pm path com.todoist
  ```

  Команда вернёт список сплитов (base.apk + config-сплиты под архитектуру/локаль/
  плотность экрана), например:
  ```
  package:/data/app/~~xxxx==/com.todoist-yyyy==/base.apk
  package:/data/app/~~xxxx==/com.todoist-yyyy==/split_config.ru.apk
  package:/data/app/~~xxxx==/com.todoist-yyyy==/split_config.arm64_v8a.apk
  ```

  Каждый пулните на диск (`adb pull <путь>`), затем соберите сплиты в единый
  устанавливаемый APK (например, инструментом APKEditor: `merge`). Готовый файл
  положите в `mobile/src/test/resources/apps/todoist.apk` — этот путь читается
  из `local.apk.path` в `common.properties` и используется только при запуске
  с флагом `-Dclean.install=true`; по умолчанию тесты используют уже
  установленное на устройстве приложение и APK не требуется вовсе.
- Путь задаётся ключом `local.apk.path`; APK ставится при запуске с флагом `-Dclean.install=true`, иначе используется уже установленное приложение.
- Для `:mobile:android` — аккаунт BrowserStack и ключи `bs.*` в `local.properties`.

### Отчёт Allure

Результаты складываются в `allure-results`, файлы `categories.json` и `environment.properties` копируются туда автоматически после каждого тестового таска. Открывать сгенерированный `index.html` двойным кликом (`file://`) нельзя: данные подгружаются через `fetch` и блокируются браузером (CORS). Используйте встроенный сервер:

```
./gradlew allureServe
```

Отчёт последнего прогона в CI: https://rarhg.github.io/todoist-automation-framework/

## CI

GitHub Actions (`.github/workflows/ci.yml`) запускает модули `api` и `db`, собирает результаты обоих в один отчёт Allure и публикует его на GitHub Pages.

- Для `db` нужен секрет репозитория `API_TOKEN`.
- Токен маскируется в результатах Allure перед публикацией отчёта.
- Web и mobile в CI не запускаются: web требует свежую сессию (капча), mobile — устройство и Appium. Оба слоя запускаются локально.

## Принятые решения

**Монорепозиторий.** API, Web, Mobile и DB лежат в одном репозитории с общей конфигурацией и единым отчётом.

**WireMock и тег `live`.** Основная часть API-тестов идёт на WireMock: они быстрые и не зависят от внешнего сервиса. Проверка авторизации (`AuthApiTest`, теги `api` и `live`) сделана на реальном API, потому что мок вернул бы 401 просто потому, что так настроен.

**Параллельность только у API.** Параллельное выполнение (`junit.jupiter.execution.parallel.enabled`) включено только для модуля `:api`. Каждый тестовый класс поднимает собственный изолированный `WireMockServer` на случайном порту (`BaseApiTest`, `@TestInstance(PER_CLASS)`), поэтому классы не пересекаются. Внутри класса стабы разных тестов на один эндпоинт не должны конфликтовать: позитивные стабы имеют приоритет 1, негативные служат запасными. В web-слое параллельность отключена осознанно: тесты используют общий тестовый аккаунт и один Inbox, и параллельные потоки создают гонки за DOM и данные. Для включения потребуются отдельные аккаунты на поток либо изоляция сценариев в отдельных проектах (так уже устроен `ProjectUiTest`).

**Отключённый `LoginTest`.** Форма входа Todoist защищена капчей и отклоняет автоматизированный браузер, поэтому класс помечен `@Disabled`, а сценарии входа проверяются вручную (см. ручные тест-кейсы).

**Очистка данных в mobile.** `BaseMobileSuite` после каждого теста удаляет все активные задачи аккаунта. Это сделано намеренно: тестовый аккаунт используется только автотестами, а mobile-тесты не запускаются параллельно. Не запускайте mobile одновременно с web и db на одном аккаунте.

**Вход в web без формы.** Сессия сохраняется вручную и подставляется в браузер (`CookieAuthManager`, `SaveSessionTool`), потому что автоматический логин блокируется капчей.

## Ручное тестирование

