package io.dockovpn.githubstars.config

import com.typesafe.config.Config
import pureconfig.ConfigSource
import pureconfig.ConfigSource.default
import pureconfig.generic.auto._

case class AppConfig(
  githubStarsCounterConfig: GithubStarsCounterConfig,
  slickConfig: Config,
)

object AppConfig {
  
  private lazy val appConf = {
    val env = sys.env.getOrElse("DLOGIN_ENV", "dev")
    val confName = s"application-$env.conf"
    val source = default(ConfigSource.resources(confName))
    
    val slickConfig: Config = source.config().map { v =>
      ConfigSource.fromConfig(v.getConfig("slick")).config()
    }.toOption.get.toOption.get
    
    val githubStarsCounterConfig: GithubStarsCounterConfig = source.config().map { v =>
      ConfigSource.fromConfig(v.getConfig("github-stars-counter")).loadOrThrow[GithubStarsCounterConfig]
    }.toOption.get
    
    new AppConfig(githubStarsCounterConfig, slickConfig)
  }
  
  def apply(): AppConfig = appConf
}
