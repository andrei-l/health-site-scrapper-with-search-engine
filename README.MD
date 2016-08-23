# Web Services application which scraps NHS Choices website and provide smart Search Engine against it

### Project details
This is a Scala based project with Spring Boot and ElasticSearch underneath. To view the API please open `http://localhost:19000/swagger-ui.html` and check `nhs-choices-api-endpoint`. You can use this UI to operate with this scrapper and search engine. It has 3 major methods:

* `GET /nhs-conditions/cache` - will a) return cache from memory stored in APP b) if there is no cache store in APP it will load cache from file nhs-choices.json, will update APP memory, will update ElasticSearch Index c) if there is no file it will scrap the website, update file, update APP memory, will update ElasticSearch Index

* `POST /nhs-conditions/cache/reload` - it will scrap the website, update file, update APP memory, will update ElasticSearch Index

* `GET /nhs-conditions?q=<query>` - will perform search against ElasticSearch Index

Size of scrapped website in json approximately = 10.8mb
Search queries almost always provide best match, further ElasticSearch Index configuration would provide better results


### Project Build Details:
To build application write `sbt oneJar` it will create a runnable jar file which you can run via
`java -jar nhs-choices_2.11-1.0-one-jar.jar`


### Implementation Details:
Frameworks\Software used: SBT + Scala + Spring Boot + ElasticSearch + ScalaScrapper + Jackson

There is a test which performs complete scrapping process of few pages and return result (emulates `GET /nhs-conditions/cache` request)