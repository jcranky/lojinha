#!/usr/bin/env sh

git pull
play clean compile test stage
sudo env PATH=$PATH play stop
sudo -b env PATH=$PATH ./target/start -Dhttp.port=80 -DapplyEvolutions.default=true
