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

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{ JsError, JsSuccess, JsValue }
import uk.gov.hmrc.securemessage.helpers.Resources
import uk.gov.hmrc.securemessage.models.core.Conversation

class ConversationRequestSpec extends PlaySpec {

  "A full conversation API request model" should {
    "be converted correctly to a core conversation model" in {
      val formatter = ISODateTimeFormat.dateTime()
      val dateInString = "2020-11-10T15:00:01.000Z"
      val dateTime = DateTime.parse(dateInString, formatter)
      val fullConversationRequestJson: JsValue = Resources.readJson("model/api/create-conversation-full.json")
      fullConversationRequestJson.validate[ConversationRequest] match {
        case s: JsSuccess[ConversationRequest] =>
          val conversationRequest: ConversationRequest = s.getOrElse(fail("Unable to get conversation"))
          val conversation = conversationRequest.asConversationWithCreatedDate("D-80542-20201120", dateTime)
          val expectedConversationJson: JsValue = Resources.readJson("model/core/conversation-full.json")
          expectedConversationJson.validate[Conversation] match {
            case success: JsSuccess[Conversation] =>
              val expectedConversation = success.getOrElse(fail("Unable to get conversation"))
              conversation mustEqual expectedConversation
            case _: JsError => fail("There was a problem reading the core model JSON file")
          }
        case _: JsError => fail("There was a problem reading the api model JSON file")
      }
    }
  }

  "A minimal conversation API request model" should {
    "be converted correctly to a core conversation model" in {
      val formatter = ISODateTimeFormat.dateTime()
      val dateInString = "2020-11-10T15:00:01.000Z"
      val dateTime = DateTime.parse(dateInString, formatter)
      val fullConversationRequestJson: JsValue = Resources.readJson("model/api/create-conversation-minimal.json")
      fullConversationRequestJson.validate[ConversationRequest] match {
        case s: JsSuccess[ConversationRequest] =>
          val conversationRequest: ConversationRequest = s.getOrElse(fail("Unable to get conversation"))
          val conversation = conversationRequest.asConversationWithCreatedDate("D-80542-20201120", dateTime)
          val expectedConversationJson: JsValue = Resources.readJson("model/core/conversation-minimal.json")
          expectedConversationJson.validate[Conversation] match {
            case success: JsSuccess[Conversation] =>
              val expectedConversation = success.getOrElse(fail("Unable to get conversation"))
              conversation mustEqual expectedConversation
            case _: JsError => fail("There was a problem reading the core model JSON file")
          }
        case _: JsError => fail("There was a problem reading the api model JSON file")
      }
    }
  }

}
