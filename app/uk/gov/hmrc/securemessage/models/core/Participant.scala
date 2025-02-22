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

package uk.gov.hmrc.securemessage.models.core

import cats.data.NonEmptyList
import org.joda.time.DateTime
import play.api.libs.json.JodaReads.jodaDateReads
import play.api.libs.json.JodaWrites.jodaDateWrites
import play.api.libs.json.{ Format, Json }
import uk.gov.hmrc.securemessage.models.utils.NonEmptyListOps.nonEmptyListFormat
import uk.gov.hmrc.emailaddress._
import uk.gov.hmrc.emailaddress.PlayJsonFormats._

final case class Participant(
  id: Int,
  participantType: ParticipantType,
  identifier: Identifier,
  name: Option[String],
  email: Option[EmailAddress],
  parameters: Option[Map[String, String]],
  readTimes: Option[List[DateTime]])
object Participant {
  private val dateFormatString = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  implicit val dateFormat: Format[DateTime] =
    Format(jodaDateReads(dateFormatString), jodaDateWrites(dateFormatString))

  implicit val participantFormat: Format[Participant] = Json.format[Participant]
}

final case class Participants(participants: NonEmptyList[Participant])
object Participants {

  implicit val emailAddressFormat: Format[EmailAddress] =
    Format(emailAddressReads, emailAddressWrites)

  implicit val participantsFormat: Format[Participants] = Json.format[Participants]

}
