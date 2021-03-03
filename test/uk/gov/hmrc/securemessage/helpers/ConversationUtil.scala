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

package uk.gov.hmrc.securemessage.helpers

import cats.data.NonEmptyList
import org.joda.time.DateTime
import uk.gov.hmrc.securemessage.controllers.models.generic._
import uk.gov.hmrc.securemessage.models.core.Language.English
import uk.gov.hmrc.securemessage.models.core._
import uk.gov.hmrc.emailaddress._

object ConversationUtil {

  def getConversationRequest(withEmailAddress: Boolean): ConversationRequest =
    ConversationRequest(
      Sender(System(SystemIdentifier("cdcm", "123"), "CDS Exports", None)),
      List(
        Recipient(
          Customer(
            CustomerEnrolment("HMRC-CUS-ORG", "EORINumber", "GB1234567890"),
            Some("Joe Bloggs"),
            if (withEmailAddress) Some(EmailAddress("joebloggs@test.com")) else None))),
      Alert("templateId", None),
      None,
      "Test",
      "base64encode",
      None
    )

  @SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
  def getFullConversation(
    id: String,
    enrolmentKey: String,
    enrolmentName: String,
    enrolmentValue: String,
    tags: Option[Map[String, String]] = Some(
      Map(
        "sourceId"         -> "CDCM",
        "caseId"           -> "D-80542",
        "queryId"          -> "D-80542-20201120",
        "mrn"              -> "DMS7324874993",
        "notificationType" -> "CDS Exports"
      ))
  ): Conversation =
    Conversation(
      "cdcm",
      id,
      ConversationStatus.Open,
      tags,
      "MRN: 19GB4S24GC3PPFGVR7",
      English,
      List(
        Participant(
          1,
          ParticipantType.System,
          Identifier("CDCM", "D-80542-20201120", None),
          Some("CDS Exports Team"),
          None,
          Some(Map("caseId" -> "D-80542", "queryId" -> "D-80542-20201120")),
          None
        ),
        Participant(
          2,
          ParticipantType.Customer,
          Identifier(enrolmentName, enrolmentValue, Some(enrolmentKey)),
          Some("Joe Bloggs"),
          Some(EmailAddress("joebloggs@test.com")),
          None,
          None
        )
      ),
      NonEmptyList.one(
        Message(
          1,
          new DateTime("2020-11-10T15:00:01.000"),
          "QmxhaCBibGFoIGJsYWg=",
          None
        )
      )
    )

  def getMinimalConversation(id: String): Conversation =
    Conversation(
      "cdcm",
      id,
      ConversationStatus.Open,
      None,
      "D-80542-20201120",
      English,
      List(
        Participant(
          1,
          ParticipantType.System,
          Identifier("CDCM", "D-80542-20201120", None),
          Some("CDS Exports Team"),
          None,
          None,
          None),
        Participant(
          2,
          ParticipantType.Customer,
          Identifier("EORINumber", "GB1234567890", Some("HMRC-CUS-ORG")),
          None,
          None,
          None,
          None)
      ),
      NonEmptyList.one(
        Message(
          1,
          new DateTime("2020-11-10T15:00:01.000"),
          "QmxhaCBibGFoIGJsYWg=",
          None
        )
      )
    )

}
