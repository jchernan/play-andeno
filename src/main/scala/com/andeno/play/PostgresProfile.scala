/*
 * Copyright (c) 2016-2021 Andeno Co. All rights reserved.
 */

package com.andeno.play

import com.github.tminglei.slickpg._
import slick.codegen.SourceCodeGenerator
import slick.model.Model
import slick.sql.SqlProfile.ColumnOption

/**
  * Extended PostgresProfile.
  */
trait PostgresProfile extends ExPostgresProfile
  with PgArraySupport
  with PgDate2Support
  with PgPlayJsonSupport {

  def pgjson: String = "jsonb"

  override val api = ExAPI

  object ExAPI extends API
    with ArrayImplicits
    with DateTimeImplicits
    with JsonImplicits
}

object PostgresProfile extends PostgresProfile

/**
  * Customized code generator.
  */
class SlickCodeGenerator(model: Model) extends SourceCodeGenerator(model) {

  override def Table = new Table(_) { // scalastyle:ignore
    override def Column = new Column(_) { // scalastyle:ignore

      override def rawType: String = {
        model.options.find(_.isInstanceOf[ColumnOption.SqlType]).flatMap {
          tpe =>
            tpe.asInstanceOf[ColumnOption.SqlType].typeName match {
              case "_text" | "text[]" => Option("List[String]")
              case "_int8" | "int8[]" => Option("List[Long]")
              case "_int4" | "int4[]" => Option("List[Int]")
              case _ => None
            }
        } getOrElse {
          model.tpe match {
            case "java.sql.Date" => "java.time.LocalDate"
            case "java.sql.Time" => "java.time.LocalTime"
            case "java.sql.Timestamp" => "java.time.OffsetDateTime"
            case _ => super.rawType
          }
        }
      }
    }
  }

  override def packageCode(
    profile: String,
    pkg: String,
    container: String,
    parentType: Option[String]) : String = {
    replaceProfile(super.packageCode(profile, pkg, container, parentType))
  }

  override def packageContainerCode(
    profile: String,
    pkg: String,
    container: String): String = {
    replaceProfile(super.packageContainerCode(profile, pkg, container))
  }

  // packageCode and packageContainerCode hard code "slick.jdbc.JdbcProfile"
  private def replaceProfile(string: String) = string.replaceAll(
    "slick.jdbc.JdbcProfile",
    "com.andeno.play.PostgresProfile"
  )
}
