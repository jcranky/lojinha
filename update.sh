#!/usr/bin/env sh

git pull
play clean compile stage

cd target/universal/stage || exit
sudo env PATH=$PATH play stop
sudo -b env PATH=$PATH ./bin/lojinha -DapplyEvolutions.default=true
cd ../../../
