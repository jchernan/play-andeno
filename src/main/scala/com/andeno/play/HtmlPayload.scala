/*
 * Copyright (c) 2016-2019 Andeno Co. All rights reserved.
 */

package com.andeno.play

import play.api.libs.json.{Json, Writes}
import play.twirl.api.Html

/**
  * Helper methods to build HTML payloads.
  */
object HtmlPayload {

  /**
    * Returns a `<script>` element with a Json payload.
    *
    * @param id The ID for the HTML element.
    * @param o The payload value.
    * @return The HTML content.
    */
  def json[T](id: String, o: T)(implicit tjs: Writes[T]): Html = {
    val payload = Json.stringify(Json.toJson(o))
    Html(s"<script type='application/json' id='$id'>$payload</script>")
  }

  /**
    * Returns a `<script>` element with the Json payload
    * of the given [[JsMessages]].
    *
    * @param id The ID for the HTML element.
    * @param messages The messages.
    * @return The HTML content.
    */
  def messages(id: String, messages: JsMessages): Html = {
    json(id, messages.json)
  }
}
