# --- STAGE 1: Build dell'applicazione ---
FROM node:lts-alpine AS builder

# Abilita pnpm tramite corepack
RUN corepack enable && corepack prepare pnpm@latest --activate

WORKDIR /app

# Copia i file di definizione dipendenze per sfruttare il caching dei layer
COPY package.json yarn.lock ./

# Installa le dipendenze
RUN yarn

# Copia il resto del codice sorgente
COPY . .

ENV VUE_APP_BACKEND='https://cosucce.go-pucci.com'
ENV VUE_APP_FIREBASE_PUBLIC_CONFIG='{"apiKey": "AIzaSyAKh-yJUoNgymQgNUaBsnc3nGjB32GXALc", "authDomain": "budget-auth-71712.firebaseapp.com", "projectId": "budget-auth-71712", "storageBucket": "budget-auth-71712.appspot.com", "messagingSenderId": "970347276867", "appId": "1:970347276867:web:cffb4155a0ab7d7d9042f6"}'

# Compila il progetto per la produzione (genera la cartella /dist)
RUN yarn quasar build

# --- STAGE 2: Serve con Nginx ---
FROM nginxinc/nginx-unprivileged:1.31-alpine AS runner

# Copia la config con ownership corretta per l'utente non-root
COPY --chown=nginx:nginx nginx.conf /etc/nginx/conf.d/default.conf

# Copia gli asset statici con ownership corretta
COPY --chown=nginx:nginx --from=builder /app/dist/spa /usr/share/nginx/html

# L'immagine gira già come utente "nginx" di default, ma è buona norma esplicitarlo
USER nginx

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
