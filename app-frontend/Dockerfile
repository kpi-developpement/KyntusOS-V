# Étape 1 : Build dyal l'application Next.js
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Étape 2 : L'environnement de Production
FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV production

# Kanjibou ghir les fichiers necessaires l'production
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public

EXPOSE 3000
CMD ["npm", "start"]