#!/usr/bin/env bash
export ES_URL=$(sudo docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' angry_noether):9200
b=80000
i=0
for d in ~/ParseHHnew/location_data/*; do
	curl -X POST $ES_URL/my_index/vacancies/$i?pretty -d @$d
	i=$((i+1))

	if [ "$i" -eq "$b" ]
	then
	  echo "break"
	  break
	fi
done
curl -X GET $ES_URL/_cat/indices?v
echo "$i"
echo "done"