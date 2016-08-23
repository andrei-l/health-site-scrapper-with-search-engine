package com.nhs.choices.search

import com.nhs.choices.bean.DiseaseArticle
import com.nhs.choices.storage.DiseaseArticleRepository
import org.elasticsearch.index.query.{MatchQueryBuilder, QueryBuilders}
import org.elasticsearch.search.sort.SortBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
import org.springframework.stereotype.Service

@Service
private class DiseasesSearchServiceImpl @Autowired()(diseaseArticleRepository: DiseaseArticleRepository) extends DiseasesSearchService {
  override def searchForDisease(keywords: String): Option[DiseaseArticle] = {
    val queryBuilder = QueryBuilders
      .matchQuery("_all", keywords)
      .operator(MatchQueryBuilder.Operator.AND)

    val query = new NativeSearchQueryBuilder()
      .withQuery(queryBuilder)
      .withSort(SortBuilders.scoreSort())
      .build()
    Option(diseaseArticleRepository.search(query).iterator()).filter(_.hasNext).map(_.next())
  }
}

trait DiseasesSearchService {
  def searchForDisease(keywords: String): Option[DiseaseArticle]
}
