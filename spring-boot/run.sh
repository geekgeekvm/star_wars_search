docker build -t startwars_search .
docker build -t startwars_search_angular .

docker run -p 8080:8080 startwars_search
docker run -p 8080:80 startwars_search_angular