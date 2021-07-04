FROM node:14.16.1 as angular
WORKDIR /app
COPY package.json /app
RUN npm i -D typescript@3.4.5
RUN npm install --silent
COPY . .
RUN npm run build 

FROM nginx:alpine
VOLUME /var/cache/nginx
COPY --from=angular app/dist/awschallenger-ui /usr/share/nginx/html
COPY ./config/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80

# docker build -t desafio-bootcamp .
# docker run -p 8081:80 desafio-bootcamp