# Build stage
FROM node:18-alpine AS builder

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

# Production stage
FROM node:18-alpine

WORKDIR /app

# Install serve to run the production build
RUN npm install -g serve

COPY --from=builder /app/build ./build

ENV REACT_APP_API_URL=http://localhost:8080/api

EXPOSE 3000

CMD ["serve", "-s", "build", "-l", "3000"]
