FROM node:20-alpine

WORKDIR /app

COPY package.json .

RUN npm install

COPY . .

EXPOSE ${PORT}

CMD ["sh", "-c", "npm run start:prod"]
