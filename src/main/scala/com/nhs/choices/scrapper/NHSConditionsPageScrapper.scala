package com.nhs.choices.scrapper

import ch.sentric.URL
import com.nhs.choices.bean.{Disease, DiseaseArticle}
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{attr => _, element => _, text => _}
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Qualifier}
import org.springframework.stereotype.Component

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

@Component
private class NHSConditionsPageScrapperImpl @Autowired()(browser: Browser, @Qualifier("nhsBaseURL") nhsBaseURL: String)
  extends NHSConditionsPageScrapper {

  private final val Logger = LoggerFactory.getLogger(getClass)

  private final val ConditionsPath = "conditions"
  private final val ConditionsPagesLocation = s"$ConditionsPath/Pages"


  override def scrapPages(): Seq[Disease] = {
    def removePagesWithSameUrl(pages: Iterable[(String, String)]): Iterable[(String, String)] = pages.groupBy(_._2).map(_._2.head)

    val alphabetLinks: Iterable[String] = scrapRootConditionsPage()

    val allDiseasesRootPages = removePagesWithSameUrl(alphabetLinks.flatMap(scrapAlphabetIndexPage))
      .filter(_._2.toLowerCase.contains(ConditionsPath))
      .map(linkNameToUrl => linkNameToUrl._1 -> buildUrlToScrap(linkNameToUrl._2))

    val result = allDiseasesRootPages map {
      case (linkName, url) => for {
        page <- tryGet(url)
        content <- extractContent(page)
        contentText = groupParagraphsIntoText(content.children)
        articles <- page >?> element("div#ctl00_PlaceHolderMain_articles ul")
        otherArticles = convertToScrappedDiseaseArticle(extractLinksToOtherArticles(articles.children))

      } yield Disease(linkName, List(DiseaseArticle(url, linkName, contentText)) ++ otherArticles)
    }

    result.flatten.toSeq
  }


  private def scrapRootConditionsPage(): Iterable[String] = {
    for {
      page <- tryGet(s"$nhsBaseURL/$ConditionsPagesLocation/hub.aspx")
      indexSection <- page >> element("div#haz-mod1")

      alphabetLinkItem <- indexSection >> element("ul") children

      link <- alphabetLinkItem >?> element("li > a")
      url = link >> attr("href")("a")
    } yield url
  }

  private def scrapAlphabetIndexPage(alphabetLinkUrl: String): Iterable[(String, String)] = {
    for {
      page <- tryGet(buildUrlToScrap(s"/$ConditionsPagesLocation/$alphabetLinkUrl"))
      indexSectionClear <- page >> element("div#haz-mod5 div.clear") children

      indexSectionClearItem <- indexSectionClear children

      indexSectionClearSubItem <- indexSectionClearItem children

      everyLink <- extractSubLinks(indexSectionClearSubItem) ++ (indexSectionClearSubItem >?> element("li > a")).toIterable

      url = everyLink >> attr("href")("a")
      linkName = everyLink >> text("a")
    } yield linkName -> url
  }

  private def extractSubLinks(linkSection: Element): Iterable[Element] = {
    val result = for {
      subLinkSection <- linkSection >?> element("ul")

      links = for {
        subLink <- subLinkSection children

        link <- subLink >?> element("li > a")
      } yield link

    } yield links

    result.getOrElse(Iterable.empty)
  }


  private def extractLinksToOtherArticles(articlesElements: Iterable[Element]): Iterable[(String, String)] =
    for {
      articleElement <- articlesElements
      link <- articleElement >?> element("li > span > a")
      url = link >> attr("href")("a")
      linkName = link >> text("a")
    } yield linkName -> url

  private def convertToScrappedDiseaseArticle(articleNameToUrl: Iterable[(String, String)]): Iterable[DiseaseArticle] =
    for {
      (articleName, articleUrl) <- articleNameToUrl
      urlToScrap = buildUrlToScrap(articleUrl)
      page <- tryGet(urlToScrap)
      content <- extractContent(page)
      contentText = groupParagraphsIntoText(content.children)
    } yield DiseaseArticle(urlToScrap, articleName, contentText)


  private def tryGet(url: String): Iterable[Element] = {
    Logger.info(s"Scrapping $url")
    val triedDocument: Try[Iterable[Element]] = Try(browser.get(url))

    triedDocument match {
      case Success(result) => result
      case Failure(ex) =>
        Logger.error(s"Failed to scrap $url: ${ex.getMessage}")
        Iterable.empty
    }
  }

  private def buildUrlToScrap(url: String): String = {
    val urlWithBaseSite = if (url.startsWith(nhsBaseURL)) url else s"$nhsBaseURL/$url"
    new URL(urlWithBaseSite).getRepairedUrl
  }

  private def extractContent(page: Element): Option[Element] =
    page >?> element("body div.wrap div.content-wrap div.row div.col div.main-content")

  private def groupParagraphsIntoText(paragraphsElements: Iterable[Element]): String =
    paragraphsElements.filter(_.tagName == "p").map(_.text).mkString(";")
}

trait NHSConditionsPageScrapper {
  def scrapPages(): Seq[Disease]
}
