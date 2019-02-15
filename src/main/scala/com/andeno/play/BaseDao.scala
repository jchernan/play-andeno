/*
 * Copyright (c) 2016-2018 Andeno Co. All rights reserved.
 */

package com.andeno.play

import java.sql.{Date, Time, Timestamp}
import java.time.{LocalDate, LocalTime, ZoneOffset, ZonedDateTime}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import scala.language.implicitConversions

/**
  * Base implementation for a DAO.
  */
class BaseDao(val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

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
    (updateFunction: (U, Timestamp) => U)
    (implicit executionContext: ExecutionContext): Future[Int] = {
    for {
      timestamp <- db.run(now())
      row <- db.run(findQuery.result.headOption)
      updated <- row.fold(Future.successful(0)) { r =>
        db.run(findQuery.update(updateFunction(r, timestamp)))
      }
    } yield updated
  }

  implicit def dateToSql(localDate: LocalDate): Date = Date.valueOf(localDate)

  implicit def timeToSql(localTime: LocalTime): Time = Time.valueOf(localTime)

  implicit def sqlToDate(date: Date): LocalDate = date.toLocalDate

  implicit def sqlToTime(timestamp: Timestamp): ZonedDateTime = {
    ZonedDateTime.of(timestamp.toLocalDateTime, ZoneOffset.UTC)
  }

  def numeric(string: String): String = {
    string.replaceAll("[^0-9]", "")
  }

  protected def now() = sql"select now()".as[Timestamp].head
}
