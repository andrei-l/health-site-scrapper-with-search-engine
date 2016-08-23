package com.nhs.choices.bean

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.{Document, Setting}

@Setting(settingPath = "/settings/elasticsearch-settings.json")
@Document(indexName = "disease", `type` = "disease", shards = 1, replicas = 0, refreshInterval = "-1")
@JsonIgnoreProperties(Array("id"))
case class DiseaseArticle(url: String,
                          articleName: String,
                          articleContent: String,
                          @Id id: String = System.currentTimeMillis().toString)
