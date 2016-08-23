package com.nhs.choices.storage

import java.io.{File, PrintWriter}
import java.util.concurrent.atomic.AtomicReference

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhs.choices.bean.{Disease, DiseaseArticle}
import com.nhs.choices.scrapper.NHSConditionsPageScrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._
import scala.io.Source


@Component
private class NHSConditionsPageStorageImpl @Autowired()(nhsConditionsPageScrapper: NHSConditionsPageScrapper,
                                                        diseaseArticleRepository: DiseaseArticleRepository,
                                                        objectMapper: ObjectMapper,
                                                        elasticsearchTemplate: ElasticsearchTemplate)
  extends NHSConditionsPageStorage {

  private final val JsonCacheLocation = new File(".", "nhs-choices.json")

  private final val Cache = new AtomicReference[Option[String]](None)

  override def loadCache(): String = Cache.get().getOrElse({
    this.synchronized {
      Cache.get().orElse(loadFromFile()).getOrElse(reloadCache())
    }
  })

  override def reloadCache(): String = {
    val scrappedDiseases = nhsConditionsPageScrapper.scrapPages()
    reloadElasticSearchContent(scrappedDiseases)
    val scrappedDiseasesJson = objectMapper.writeValueAsString(scrappedDiseases)
    Cache.set(Option(scrappedDiseasesJson))
    new PrintWriter(JsonCacheLocation) {
      write(scrappedDiseasesJson)
      close()
    }
    scrappedDiseasesJson
  }


  private def loadFromFile(): Option[String] = {
    this.synchronized {
      if (JsonCacheLocation.exists() && JsonCacheLocation.isFile) {
        val json = Option(Source.fromFile(JsonCacheLocation).mkString)
        val diseases: Seq[Disease] = objectMapper.readValue(json.get, objectMapper.getTypeFactory.constructCollectionLikeType(classOf[Seq[Disease]], classOf[Disease]))
        reloadElasticSearchContent(diseases)
        Cache.set(json)
        json
      }
      else None
    }
  }

  private def reloadElasticSearchContent(scrappedDiseases: Seq[Disease]): Unit = {
    diseaseArticleRepository.deleteAll()
    elasticsearchTemplate.deleteIndex(classOf[DiseaseArticle])
    elasticsearchTemplate.createIndex(classOf[DiseaseArticle])
    elasticsearchTemplate.refresh(classOf[DiseaseArticle])
    diseaseArticleRepository.save(scrappedDiseases.flatMap(_.diseaseArticles).asJava)
  }

}

trait NHSConditionsPageStorage {
  def loadCache(): String

  def reloadCache(): String
}
