package com.nhs.choices.api

import com.nhs.choices.bean.DiseaseArticle
import com.nhs.choices.search.DiseasesSearchService
import com.nhs.choices.storage.NHSConditionsPageStorage
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{attr => _, element => _, text => _}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, MediaType}
import org.springframework.web.bind.annotation._
import org.springframework.web.servlet.ModelAndView

import scala.language.postfixOps

@RestController
class NHSChoicesAPIEndpoint @Autowired()(nhsConditionsPageStorage: NHSConditionsPageStorage, diseasesSearchService: DiseasesSearchService) {
  @GetMapping(value = Array("/"))
  def index(): ModelAndView = new ModelAndView("redirect:swagger-ui.html")

  @PostMapping(value = Array("/nhs-conditions/cache/reload"), produces = Array(MediaType.APPLICATION_JSON_VALUE))
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  def reloadCache(): String = {
    nhsConditionsPageStorage.reloadCache()
  }

  @GetMapping(value = Array("/nhs-conditions/cache"), produces = Array(MediaType.APPLICATION_JSON_VALUE))
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  def loadCache(): String = nhsConditionsPageStorage.loadCache()

  @GetMapping(value = Array("/nhs-conditions"), produces = Array(MediaType.APPLICATION_JSON_VALUE))
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  def searchDisease(@RequestParam("q") query: String): Option[DiseaseArticle] = diseasesSearchService.searchForDisease(query)
}
