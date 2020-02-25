/*
 * Copyright (c) 2016-2020 Andeno Co. All rights reserved.
 */

package com.andeno.play

import java.io.File
import javax.inject.{Inject, Singleton}

import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}
import play.api.libs.json._

/**
  * Alternative implementation of an I18n module.
  */
class I18nModule extends Module {

  def bindings(
    environment: Environment,
    configuration: Configuration): Seq[Binding[_]] = {
    Seq(bind[JsMessagesApi].to[DefaultJsMessagesApi])
  }
}

case class JsMessages(json: JsValue) {
  def translate(key: String): String = {
    val path = JsPath(key.split('.').map(KeyPathNode).toList)
    json.transform(path.json.pick[JsString]) match {
      case JsSuccess(message, _) => message.as[String]
      case _ => key
    }
  }
}

trait JsMessagesApi {
  def apply(group: String): JsMessages
}

@Singleton
class DefaultJsMessagesApi @Inject() (
  environment: Environment,
  configuration: Configuration) extends JsMessagesApi {

  private val LangsKey = "play.i18n.langs"
  private val Langs = configuration.get[Seq[String]](LangsKey)
  private val DefaultLang = Langs.head
  private val CommonGroup = "common"

  private val messages = loadAllMessages

  def apply(group: String): JsMessages = apply(group, DefaultLang)

  def apply(group: String, lang: String ): JsMessages =
    messages(s"$group.$lang")

  protected def loadAllMessages: Map[String, JsMessages] = {
    val messages = (for {
      file <- listFiles
      lang <- Langs
      if file.endsWith(s".$lang")
      messages = loadMessages(file)
    } yield (file, messages)).toMap
    messages.map {
      case (g, m) if !g.startsWith(CommonGroup) =>
        val lang = Langs.filter(l => g.endsWith(s".$l")).head
        val common = messages(s"$CommonGroup.$lang")
        (g, JsMessages(m.json.as[JsObject] ++ common.json.as[JsObject]))
      case x => x
    }
  }

  protected def listFiles: Array[String] = {
    environment.resource("messages")
      .map(r => new File(r.getPath).listFiles)
      .getOrElse(Array())
      .map(_.getName)
  }

  protected def loadMessages(file: String): JsMessages = {
    environment.resourceAsStream(s"messages/$file") match {
      case Some(stream) => JsMessages(Json.parse(stream))
      case None => throw new Exception("Missing I18n messages file")
    }
  }
}
