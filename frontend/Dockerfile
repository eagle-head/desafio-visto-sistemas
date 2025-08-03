# Multi-stage Dockerfile for Angular application
# Optimized for minimal image size and performance

# Stage 1: Build Angular application
FROM node:20.19-alpine AS builder
WORKDIR /app

# Copy package files first for better layer caching
COPY package.json package-lock.json ./

# Install all dependencies (dev dependencies needed for Angular CLI build)
RUN npm ci --silent && \
    npm cache clean --force

# Copy source code
COPY . .

# Build Angular application for production (Angular 20+ with esbuild)
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:1.27-alpine

# Install wget for health checks
RUN apk add --no-cache wget

# Remove default nginx files
RUN rm -rf /usr/share/nginx/html/*

# Copy built Angular application (Angular 20+ puts files in browser/ subdirectory)
COPY --from=builder /app/dist/frontend/browser/ /usr/share/nginx/html/

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Create non-root user for enhanced security
RUN addgroup -g 1001 -S nginx-user && \
    adduser -u 1001 -G nginx-user -S -D -H nginx-user

# Set proper permissions for nginx to run as non-root
RUN chown -R nginx-user:nginx-user /usr/share/nginx/html && \
    chown -R nginx-user:nginx-user /var/cache/nginx && \
    chown -R nginx-user:nginx-user /var/log/nginx && \
    chown -R nginx-user:nginx-user /etc/nginx/conf.d && \
    touch /var/run/nginx.pid && \
    chown -R nginx-user:nginx-user /var/run/nginx.pid

# Switch to non-root user
USER nginx-user

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Start nginx in foreground
CMD ["nginx", "-g", "daemon off;"]