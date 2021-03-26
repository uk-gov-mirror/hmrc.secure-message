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

package uk.gov.hmrc.securemessage.controllers.model.cdcm.write

import cats.data.NonEmptyList
import org.joda.time.DateTime
import play.api.libs.json.{ Format, Json, Reads }
import uk.gov.hmrc.securemessage.controllers.model.common
import uk.gov.hmrc.securemessage.controllers.model.common.CustomerEnrolment
import uk.gov.hmrc.securemessage.controllers.model.common.write.Recipient
import uk.gov.hmrc.securemessage.models.core
import uk.gov.hmrc.securemessage.models.core._
import uk.gov.hmrc.time.DateTimeUtils

/**
  * @param tags metadata sent to the UI
  * */
final case class CdcmConversation(
  sender: CdcmSender,
  recipients: List[Recipient],
  alert: common.Alert,
  tags: CdcmTags,
  subject: String,
  message: String,
  language: Option[String])
    extends DateTimeUtils {

  def asConversationWithCreatedDate(client: String, conversationId: String, created: DateTime): Conversation = {
    val initialMessage = Message(1, created, message)
    val initialParticipants = getSenderParticipant(client, conversationId, sender.system) :: getRecipientParticipants(
      recipients)
    Conversation(
      client,
      conversationId,
      ConversationStatus.Open,
      Some(Map("mrn" -> tags.mrn, "notificationType" -> tags.notificationType.entryName)), //TODO: remove option as empty map can take place of that
      subject,
      getLanguage(language),
      initialParticipants,
      NonEmptyList.one(initialMessage),
      core.Alert(alert.templateId, alert.parameters)
    )
  }

  private def getLanguage(maybeLanguage: Option[String]): Language =
    maybeLanguage match {
      case Some(lang) => Language.withNameInsensitiveOption(lang).getOrElse(Language.English)
      case _          => Language.English

    }
  private def getSenderParticipant(client: String, conversationId: String, senderSystem: CdcmSystem): Participant =
    Participant(
      1,
      ParticipantType.System,
      Identifier(client, conversationId, None),
      Some(senderSystem.display),
      None,
      None,
      None
    )

  private def getRecipientParticipants(recipients: List[Recipient]): List[Participant] =
    recipients.zip(Stream from 2) map {
      case (recipient, id) =>
        val customer = recipient.customer
        Participant(
          id,
          ParticipantType.Customer,
          getCustomerIdentifier(customer.enrolment),
          customer.name,
          customer.email,
          None,
          None)
    }

  private def getCustomerIdentifier(enrolment: CustomerEnrolment): Identifier =
    Identifier(enrolment.name, enrolment.value, Some(enrolment.key))

}
object CdcmConversation {
  implicit val conversationRequestReads: Reads[CdcmConversation] =
    Json.reads[CdcmConversation]
}

final case class CdcmSystem(display: String)
object CdcmSystem {
  implicit val systemReads: Reads[CdcmSystem] = Json.reads[CdcmSystem]
}

final case class CdcmSender(system: CdcmSystem)
object CdcmSender {
  implicit val senderReads: Reads[CdcmSender] = Json.reads[CdcmSender]
}

case class CdcmTags(mrn: String, notificationType: CdcmNotificationType)

object CdcmTags {
  implicit val tagsFormats: Format[CdcmTags] = Json.format[CdcmTags]
}
