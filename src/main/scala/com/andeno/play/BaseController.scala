/*
 * Copyright (c) 2016-2023 Andeno Co. All rights reserved.
 */

package com.andeno.play

import javax.inject.Inject

import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

/**
  * Base implementation for the controllers.
  */
trait BaseController extends play.api.mvc.BaseController {

  protected val DefaultPageSize = 10
  protected val One = Some(Page(1, 1))

  def jsonResult[A](response: Future[A])
    (implicit writes: Writes[A],
      executionContext: ExecutionContext): Future[Result] = {
    recover {
      response.map(value => Ok(Json.toJson(value)))
    }
  }

  def jsonOptionResult[A](response: Future[Option[A]])
    (implicit writes: Writes[A],
      executionContext: ExecutionContext): Future[Result] = {
    recover {
      response.map {
        case Some(value) => Ok(Json.toJson(value))
        case None => NotFound
      }
    }
  }

  def noBodyResult[A](response: Future[Option[A]])
    (implicit executionContext: ExecutionContext): Future[Result] = {
    recover {
      response.map {
        case Some(_) => Ok
        case None => NotFound
      }
    }
  }

  def recover(result: Future[Result])
    (implicit executionContext: ExecutionContext): Future[Result] = {
    result.recover {
      case _: Exception => InternalServerError
    }
  }

  def buildPage(number: Option[Int], count: Option[Int]): Option[Page] = {
    (number, count) match {
      case (Some(n), Some(c)) => Some(Page(n, c))
      case (Some(n), None) => Some(Page(n, DefaultPageSize))
      case (None, Some(c)) => Some(Page(1, c))
      case _ => None
    }
  }
}

/**
  * Represents the pagination parameters in a request.
  */
case class Page(
  number: Int,
  count: Int
)

/**
  * Extends [[ActionBuilder]] with a convenience method to parse json.
  */
abstract class ParseActionBuilder[+R[_]](val parsers: PlayBodyParsers)
  extends ActionBuilder[R, AnyContent] {
  self =>

  def json[A](block: R[A] => Future[Result])
    (implicit reader: Reads[A]): Action[A] = self.async(parsers.json[A])(block)

  override def andThen[Q[_]](
    other: ActionFunction[R, Q]
  ): ParseActionBuilder[Q] = new ParseActionBuilder[Q](parsers) {
    def parser = self.parser
    def executionContext = self.executionContext
    def invokeBlock[A](request: Request[A], block: Q[A] => Future[Result]) =
      self.invokeBlock[A](request, other.invokeBlock[A](_, block))
    override protected def composeParser[A](
      bodyParser: BodyParser[A]): BodyParser[A] =
      self.composeParser(bodyParser)
    override protected def composeAction[A](action: Action[A]): Action[A] =
      self.composeAction(action)
  }
}

/**
  * Simple action that extends [[ParseActionBuilder]].
  */
class ParseAction @Inject() (override val parsers: PlayBodyParsers)
  (implicit val executionContext: ExecutionContext)
  extends ParseActionBuilder[Request](parsers) {

  def parser: BodyParser[AnyContent] = parsers.default

  def invokeBlock[A](
    request: Request[A],
    block: (Request[A]) => Future[Result]): Future[Result] = block(request)
}
