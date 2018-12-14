#!/usr/bin/env bash
export ES_URL=$(sudo docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' angry_noether):9200

#curl -X PUT $ES_URL/customer?pretty
#curl -X PUT $ES_URL/customer/external/1?pretty -H 'Content-Type: application/json' -d'
#{
  #"name": "John Doe"
#}
#'
#curl -X DELETE $ES_URL/customer?pretty
#curl -X GET $ES_URL/_cat/indices?v

#response=$(curl -X POST $ES_URL/new_vacancies/vacancies/1?pretty -d @new.json)
#echo $response
i = 0
for d in ~/ParseHHnew/data/*; do
	i=$((i+1))
	curl -X POST $ES_URL/test_index/vacancies/$i?pretty -d @$d
	if ["$i" -eq "2000"]
		then break;
	fi
done
curl -X GET $ES_URL/_cat/indices?v