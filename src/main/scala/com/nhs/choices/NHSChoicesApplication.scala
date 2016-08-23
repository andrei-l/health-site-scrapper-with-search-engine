package com.nhs.choices

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(Array("com.nhs.choices"))
class NHSChoicesApplication extends SpringBootServletInitializer {
  override def configure(builder: SpringApplicationBuilder): SpringApplicationBuilder =
    builder.sources(classOf[NHSChoicesApplication])
}

object NHSChoicesApplication {
  def main(args: Array[String]): Unit = {
    new SpringApplicationBuilder(classOf[NHSChoicesApplication]).run(args: _*)
  }
}
