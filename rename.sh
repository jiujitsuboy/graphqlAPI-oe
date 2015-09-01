#!/bin/bash
#sed -i  "s/%SUB_DOMAIN%/$FINAL_ENV/g" /etc/default/pp-service
for file in `ls .`
do
    if [ "$file" != "." ]; then
        echo "Renaming file : $file"
        mv "$file" "${file//pp-service/pp-lead}";  # hard code for now
    fi
done

#Change all pp-service, 0-pp-service Service recursively
#Replace all pp-service, pp_service and PP-SERVICE and PP_SERVICE in all files

