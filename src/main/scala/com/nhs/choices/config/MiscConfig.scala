package com.nhs.choices.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.{Bean, Configuration}

@Configuration
private class MiscConfig {

  @Bean
  def browser(): Browser = new JsoupBrowser

  @Bean
  @Qualifier("nhsBaseURL")
  def nhsBaseURL(): String = "http://www.nhs.uk"

  @Bean
  def scalaObjectMapper(): ObjectMapper = {
    val om = new ObjectMapper()
    om.registerModule(DefaultScalaModule)
    om
  }

}
