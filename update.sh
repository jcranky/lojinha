#!/usr/bin/env sh

git pull
play clean compile stage
sudo env PATH=$PATH play stop
sudo ./target/start -Dhttp.port=80 -DapplyEvolutions.default=true &
