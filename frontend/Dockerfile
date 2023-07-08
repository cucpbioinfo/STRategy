# build environment
FROM node:13.12.0-alpine as build
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json ./
COPY package-lock.json ./
RUN npm ci --silent
RUN npm install react-scripts@3.4.1 -g --silent
ARG REACT_APP_BACKEND_BASE_URL
ENV REACT_APP_BACKEND_BASE_URL $REACT_APP_BACKEND_BASE_URL
ARG REACT_APP_BACKEND_PORT
ENV REACT_APP_BACKEND_PORT $REACT_APP_BACKEND_PORT
COPY . ./
RUN npm run build

# production environment
FROM nginx:stable-alpine
COPY --from=build /app/build /usr/share/nginx/html
# new
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]