#!/bin/sh

echo Fetching remote database...

ssh -C root@gamespace.us mysqldump -u root gamespace | mysql -u root -D gamespace
