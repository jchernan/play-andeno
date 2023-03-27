/*
 * Copyright (c) 2016-2023 Andeno Co. All rights reserved.
 */

package com.andeno.play

import java.time.{OffsetDateTime, ZoneId}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import scala.language.implicitConversions

/**
  * Base implementation for a DAO.
  */
class BaseDao(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[PostgresProfile] {

  import dbConfig.profile.api._

  def mapResult[A, B](future: Future[A])
    (implicit conversion: A => B,
      executionContext: ExecutionContext): Future[B] = {
    future.map(conversion)
  }

  def mapOptionResult[A, B](future: Future[Option[A]])
    (implicit conversion: A => B,
      executionContext: ExecutionContext): Future[Option[B]] = {
    future.map {
      case Some(row) => Some(conversion(row))
      case None => None
    }
  }

  def mapSeqResult[A, B](future: Future[Seq[A]])
    (implicit conversion: A => B,
      executionContext: ExecutionContext): Future[Seq[B]] = {
    future.map(_.map(row => conversion(row)))
  }

  def paginate[E, U, C[_]](query: Query[E, U, C])
    (implicit page: Option[Page]): Query[E, U, C] = {
    page match {
      case Some(p) => query.drop((p.number - 1) * p.count).take(p.count)
      case None => query
    }
  }

  def update[E, U, C[_]](findQuery: Query[E, U, C])
    (updateFunction: (U, OffsetDateTime) => U)
    (implicit executionContext: ExecutionContext): Future[Int] = {
    for {
      row <- db.run(findQuery.result.headOption)
      updated <- row.fold(Future.successful(0)) { r =>
        db.run(findQuery.update(updateFunction(r, now)))
      }
    } yield updated
  }

  def numeric(string: String): String = {
    string.replaceAll("[^0-9]", "")
  }

  def now: OffsetDateTime = OffsetDateTime.now(ZoneId.of("UTC"))
}
