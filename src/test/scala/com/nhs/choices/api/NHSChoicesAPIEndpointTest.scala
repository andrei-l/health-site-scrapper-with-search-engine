package com.nhs.choices.api

import java.io.File

import com.nhs.choices.storage.DiseaseArticleRepository
import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.elasticsearch.client.Client
import org.junit.{Before, Test}
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders._
import org.springframework.test.web.servlet.result.MockMvcResultMatchers._

import scala.io.Source

@RunWith(classOf[SpringRunner])
@WebMvcTest(controllers = Array(classOf[NHSChoicesAPIEndpoint]))
class NHSChoicesAPIEndpointTest {
  @Autowired
  private val mockMvc: MockMvc = None.orNull

  @MockBean
  val diseaseArticleRepository: DiseaseArticleRepository = None.orNull

  @MockBean
  val elasticSearchClient: Client = None.orNull

  @MockBean
  val elasticSearchTemplate: ElasticsearchTemplate = None.orNull

  @MockBean
  val browser: Browser = None.orNull

  val JsonCacheLocation = new File(".", "nhs-choices.json")

  @Before
  def setup(): Unit = {
    JsonCacheLocation.delete()
  }


  @Test
  def testLoadCache(): Unit = {
    val jsoupBrowser = new JsoupBrowser()
    given(browser.get("http://www.nhs.uk/conditions/Pages/hub.aspx"))
      .willReturn(jsoupBrowser.parseFile(load("root-page.htm")))

    given(browser.get(Matchers.endsWith("http://www.nhs.uk/conditions/Pages/BodyMap.aspx?Index=A")))
      .willReturn(jsoupBrowser.parseFile(load("a-letter-page.htm")))

    given(browser.get(Matchers.endsWith("http://www.nhs.uk/conditions/Repairofabdominalaneurysm")))
      .willReturn(jsoupBrowser.parseFile(load("article-1.htm")))

    given(browser.get(Matchers.endsWith("http://www.nhs.uk/conditions/abdominal-aortic-aneurysm-screening")))
      .willReturn(jsoupBrowser.parseFile(load("article-2.htm")))

    mockMvc.perform(get("/nhs-conditions/cache").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk)
      .andExpect(content().json(Source.fromFile(load("NHSChoicesAPIEndpointTestLoadCacheExpectedResult.json")).mkString))
  }

  private def load(file: String): File = new ClassPathResource(file).getFile
}
