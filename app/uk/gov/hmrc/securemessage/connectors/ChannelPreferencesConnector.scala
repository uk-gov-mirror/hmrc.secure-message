/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.securemessage.connectors

import play.api.{ Configuration, Logging }
import play.api.http.Status.OK
import play.api.libs.json.{ JsSuccess, Json }
import uk.gov.hmrc.emailaddress.EmailAddress
import uk.gov.hmrc.emailaddress.PlayJsonFormats.emailAddressReads
import uk.gov.hmrc.http.{ HeaderCarrier, HttpClient, HttpResponse }
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.securemessage.EmailLookupError
import uk.gov.hmrc.securemessage.models.core.Identifier

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

@Singleton
@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
class ChannelPreferencesConnector @Inject()(config: Configuration, httpClient: HttpClient)(
  implicit ec: ExecutionContext)
    extends ServicesConfig(config) with Logging {

  @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
  def getEmailForEnrolment(id: Identifier)(implicit hc: HeaderCarrier): Future[Either[EmailLookupError, EmailAddress]] =
    httpClient
      .GET[HttpResponse](
        url = s"""${baseUrl("channel-preferences")}/channel-preferences/preference/email""",
        queryParams =
          Seq(("enrolmentKey", id.enrolment.fold("")(identity)), ("taxIdName", id.name), ("taxIdValue", id.value))
      )
      .map { resp =>
        resp.status match {
          case OK => parseEmail(resp.body)
          case s =>
            val errMsg = s"channel-preferences returned status: $s body: ${resp.body}"
            Left(EmailLookupError(errMsg))
        }
      }

  @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
  private def parseEmail(body: String): Either[EmailLookupError, EmailAddress] =
    Try(Json.parse(body)) match {
      case Success(v) =>
        (v \ "address").validate[EmailAddress] match {
          case JsSuccess(ev, _) => Right(ev)
          case _ =>
            val errMsg = s"could not find an email address in the response: $body"
            Left(EmailLookupError(errMsg))
        }
      case Failure(e) =>
        val errMsg = s"channel-preferences response was an invalid json: $body, error: ${e.getMessage}"
        Left(EmailLookupError(errMsg))
    }

}
