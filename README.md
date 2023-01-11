# Budget (budget-fe2)

Budget project

## Install the dependencies

```bash
yarn
# or
npm install
```

### Start the app in development mode (hot-code reloading, error reporting, etc.)

```bash
$env:VUE_APP_BACKEND='http://localhost:8080';
$env:VUE_APP_FIREBASE_PUBLIC_CONFIG='{"apiKey": "AIzaSyAKh-yJUoNgymQgNUaBsnc3nGjB32GXALc", "authDomain": "budget-auth-71712.firebaseapp.com", "projectId": "budget-auth-71712", "storageBucket": "budget-auth-71712.appspot.com", "messagingSenderId": "970347276867", "appId": "1:970347276867:web:cffb4155a0ab7d7d9042f6"}';
quasar dev
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
quasar build
```

### Customize the configuration

See [Configuring quasar.config.js](https://v2.quasar.dev/quasar-cli-vite/quasar-config-js).
