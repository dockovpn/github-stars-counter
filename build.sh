#!/usr/bin/env bash

rm -R target

./bump.sh "$@"

version=$(cat VERSION)
ver="v$version"

sbt ';build' && \
docker build -t alekslitvinenk/github-stars-counter:"$ver" -t alekslitvinenk/github-stars-counter:latest . --no-cache && \
docker push alekslitvinenk/github-stars-counter:"$ver"