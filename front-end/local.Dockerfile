FROM node:20-alpine as build

WORKDIR /app

COPY package.json .

RUN npm install

COPY . .

EXPOSE ${PORT}

CMD ["npm", "start"]
