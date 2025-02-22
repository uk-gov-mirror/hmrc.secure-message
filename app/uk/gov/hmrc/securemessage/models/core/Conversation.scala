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
import play.api.libs.json.{ Json, OFormat }
import uk.gov.hmrc.securemessage.models.utils.NonEmptyListOps._

final case class Conversation(
  client: String,
  id: String, //TODO: rename to id
  status: ConversationStatus,
  tags: Option[Map[String, String]],
  subject: String,
  language: Language,
  participants: List[Participant],
  messages: NonEmptyList[Message],
  alert: Alert
)

object Conversation {
  implicit val conversationFormat: OFormat[Conversation] = Json.format[Conversation]
}
