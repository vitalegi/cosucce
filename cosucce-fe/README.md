# Cosucce (cosucce-fe)

Cosucce FE

## Install the dependencies

```bash
yarn
# or
npm install
```

### Start the app in development mode (hot-code reloading, error reporting, etc.)

```bash
$env:APP_SELF_URL="<url where application is exposed, e.g.: https://google.com>"
$env:APP_OIDC_URL="<oidc authorization server url, e.g.: https://google.com>"
$env:APP_OIDC_CLIENT_ID="<oidc client id>"
quasar dev -m pwa
```

### Lint the files

```bash
yarn lint
# or
npm run lint
```

### Format the files

```bash
yarn format
# or
npm run format
```

### Build the app for production

```bash
$env:APP_SELF_URL="<url where application is exposed, e.g.: https://google.com>"
$env:APP_OIDC_URL="<oidc authorization server url, e.g.: https://google.com>"
$env:APP_OIDC_CLIENT_ID="<oidc client id>"
quasar build -m pwa
```

### Customize the configuration

See [Configuring quasar.config.js](https://v2.quasar.dev/quasar-cli-vite/quasar-config-js).
