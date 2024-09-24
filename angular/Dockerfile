# Use official node image as the base image
FROM node:lts AS build

ADD ./package.json /tmp/package.json
RUN cd /tmp && npm install
RUN mkdir -p /usr/local/app && cp -a /tmp/node_modules /usr/local/app/

WORKDIR /usr/local/app

# Add the source code to app
COPY . /usr/local/app/
COPY /src/config/app-config.json /usr/share/nginx/html/app-config.json

# Generate the build of the application
RUN npm run build

# Stage 2: Serve app with nginx server
# Use official nginx image as the base image
FROM nginx:latest

COPY --from=build /usr/local/app/dist/star-wars-searcher/browser /usr/share/nginx/html
COPY ./entrypoint.sh /usr/local/app/entrypoint.sh
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

# Expose ports
EXPOSE 3000 80 443 6006 4200

RUN chmod +x /usr/local/app/entrypoint.sh
ENTRYPOINT [ "/usr/local/app/entrypoint.sh" ]