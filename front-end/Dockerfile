FROM node:20-slim

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY . .
ENV PORT 3000
RUN npm install -g serve

RUN npm install

ARG REACT_APP_BASE_URL
ARG REACT_APP_DISABLE_NEW_USERS
ENV REACT_APP_BASE_URL=${REACT_APP_BASE_URL}
ENV REACT_APP_DISABLE_NEW_USERS=${REACT_APP_DISABLE_NEW_USERS}

RUN npm run build

CMD ["serve", "-s", "-l", "3000", "./build"]
