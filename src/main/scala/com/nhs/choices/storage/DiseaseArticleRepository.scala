package com.nhs.choices.storage

import com.nhs.choices.bean.DiseaseArticle
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

trait DiseaseArticleRepository extends ElasticsearchRepository[DiseaseArticle, String]
