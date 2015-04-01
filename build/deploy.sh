#!/bin/sh

echo Building jar.
/usr/local/Cellar/ant/1.9.4/libexec/bin/ant

echo Uploading jar...
scp server.jar root@teamup.jasonmirra.com:~/teamup.jar

echo Restarting server
ssh root@teamup.jasonmirra.com 'screen -S teamup -X quit;screen -S teamup -d -m java -jar teamup.jar'

echo Done.
