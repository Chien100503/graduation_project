# Build Stage
FROM node:18 AS build
WORKDIR /app

# Copy package.json and install dependencies
COPY package*.json ./
RUN npm install

# Copy source code and build the app
COPY . .
RUN npm run build

# Run Stage with Nginx
FROM nginx:1.23-alpine

# Copy the build files from the build stage to Nginx's html folder
COPY --from=build /app/build /usr/share/nginx/html

# Copy the Nginx config file into the container
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 to access the app
EXPOSE 80

# Start Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]