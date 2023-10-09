FROM timbru31/java-node:17-jre
COPY . .
RUN yarn install
RUN yarn generate
ENTRYPOINT ["yarn", "start"]
