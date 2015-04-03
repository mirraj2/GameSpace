#!/bin/sh

echo Building jar.
/usr/local/Cellar/ant/1.9.4/libexec/bin/ant

echo Uploading jar...
scp server.jar root@gamespace.us:~/gamespace.jar

echo Restarting server
ssh root@gamespace.us 'screen -S gamespace -X quit;screen -S gamespace -d -m java -jar gamespace.jar'

echo Done.
