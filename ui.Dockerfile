FROM nginx:alpine

ENV api=ws://localhost:8080/hello
ENV rest=http://localhost:8080/ping

COPY src_ui/dist/ /usr/share/nginx/html/
COPY src_ui/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]