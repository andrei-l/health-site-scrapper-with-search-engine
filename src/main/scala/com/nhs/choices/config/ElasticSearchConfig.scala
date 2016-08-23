package com.nhs.choices.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.client.Client
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.data.elasticsearch.core.{ElasticsearchTemplate, EntityMapper}

@Configuration
private class ElasticSearchConfig {
  @Bean
  def customEntityMapper(@Qualifier("scalaObjectMapper") objectMapper: ObjectMapper): EntityMapper = new EntityMapper {
    override def mapToString(`object`: scala.Any): String = objectMapper.writeValueAsString(`object`)

    override def mapToObject[T](source: String, clazz: Class[T]): T = objectMapper.readValue(source, clazz)
  }

  @Bean
  def elasticsearchTemplate(client: Client, mapper: EntityMapper): ElasticsearchTemplate =
    new ElasticsearchTemplate(client, mapper)
}
