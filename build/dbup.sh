#!/bin/sh

echo Uploading database...

mysqldump -u root gamespace | ssh -C root@gamespace.us mysql -u root -D gamespace