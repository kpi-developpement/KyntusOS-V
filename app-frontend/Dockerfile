# Étape 1 : Build dyal l'application Next.js
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# 🔥 L'HERBA HNA: Kan-st9blou l'variable mn docker-compose w kan-passiwha l'Build
ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL

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