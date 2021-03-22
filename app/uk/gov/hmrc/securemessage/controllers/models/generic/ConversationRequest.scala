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

package uk.gov.hmrc.securemessage.controllers.models.generic

import cats.data.NonEmptyList
import org.joda.time.DateTime
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{ JsPath, Json, Reads }
import uk.gov.hmrc.securemessage.models.core
import uk.gov.hmrc.securemessage.models.core._
import uk.gov.hmrc.time.DateTimeUtils
import uk.gov.hmrc.emailaddress._
import uk.gov.hmrc.emailaddress.PlayJsonFormats.emailAddressReads

final case class Alert(templateId: String, parameters: Option[Map[String, String]])
object Alert {
  implicit val alertReads: Reads[Alert] =
    Json.reads[Alert]
}

final case class CustomerEnrolment(key: String, name: String, value: String) {
  def asIdentifier: Identifier = Identifier(name, value, Some(key))
}
object CustomerEnrolment {
  implicit val enrolmentReads: Reads[CustomerEnrolment] = {
    Json.reads[CustomerEnrolment]
  }

  def parse(enrolmentString: String): CustomerEnrolment = {
    val enrolment = enrolmentString.split('~')
    CustomerEnrolment(enrolment.head, enrolment(1), enrolment.last)
  }
}

final case class Tag(key: String, value: String)
object Tag {
  implicit val tagReads: Reads[Tag] = {
    Json.reads[Tag]
  }

  def parse(tagString: String): Tag = {
    val tag = tagString.split('~')
    Tag(tag.head, tag(1))
  }
}

final case class SystemIdentifier(name: String, value: String)
object SystemIdentifier {
  implicit val identifierReads: Reads[SystemIdentifier] = Json.reads[SystemIdentifier]
}

/**
  * @param parameters metadata sent back to the sender
  * */
final case class System(identifier: SystemIdentifier, display: String, parameters: Option[Map[String, String]])
object System {
  implicit val systemReads: Reads[System] = (
    (JsPath \ "identifier").read[SystemIdentifier] and
      (JsPath \ "display").read[String] and
      (JsPath \ "parameters").readNullable[Map[String, String]]
  ).apply(System.apply _)
}

final case class Customer(enrolment: CustomerEnrolment, name: Option[String], email: Option[EmailAddress])
object Customer {
  implicit val customerReads: Reads[Customer] = Json.reads[Customer]
}

final case class Sender(system: System)
object Sender {
  implicit val senderReads: Reads[Sender] =
    Json.reads[Sender]
}

final case class Recipient(customer: Customer)
object Recipient {
  implicit val recipientReads: Reads[Recipient] =
    Json.reads[Recipient]
}

/**
  * @param tags metadata sent to the UI
  * */
final case class ConversationRequest(
  sender: Sender,
  recipients: List[Recipient],
  alert: Alert,
  tags: Option[Map[String, String]],
  subject: String,
  message: String,
  language: Option[String])
    extends DateTimeUtils {

  def asConversation(client: String, conversationId: String): Conversation =
    asConversationWithCreatedDate(client, conversationId, now)

  def asConversationWithCreatedDate(client: String, conversationId: String, created: DateTime): Conversation = {
    val initialMessage = Message(1, created, message)
    val initialParticipants = getSenderParticipant(sender.system) :: getRecipientParticipants(recipients)
    Conversation(
      client,
      conversationId,
      ConversationStatus.Open,
      if (tags.isDefined) tags else None,
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
  private def getSenderParticipant(senderSystem: System): Participant =
    Participant(
      1,
      ParticipantType.System,
      Identifier(senderSystem.identifier.name, senderSystem.identifier.value, None),
      Some(senderSystem.display),
      None,
      senderSystem.parameters,
      None
    )

  private def getRecipientParticipants(recipients: List[Recipient]): List[Participant] =
    recipients.zip(Stream from 2) map { r =>
      val customer = r._1.customer
      Participant(
        r._2,
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
object ConversationRequest {
  implicit val conversationRequestReads: Reads[ConversationRequest] =
    Json.reads[ConversationRequest]
}
