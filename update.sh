#!/usr/bin/env sh

git pull
play clean compile stage

cd target/universal/stage
sudo env PATH=$PATH play stop
sudo -b env PATH=$PATH ./bin/lojinha -DapplyEvolutions.default=true
cd ../../../
