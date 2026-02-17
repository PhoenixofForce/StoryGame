#!/bin/sh
sed -i "s|{API_URL}|${api}|g" /usr/share/nginx/html/assets/*
sed -i "s|{REST_URL}|${rest}|g" /usr/share/nginx/html/assets/*
sed -i "s|/StoryGame||g" /usr/share/nginx/html/assets/*
sed -i "s|/StoryGame||g" /usr/share/nginx/html/index.html
exec "$@"