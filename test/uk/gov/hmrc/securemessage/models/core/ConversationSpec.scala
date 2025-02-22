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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{ JsSuccess, JsValue }
import uk.gov.hmrc.securemessage.helpers.{ ConversationUtil, Resources }

class ConversationSpec extends PlaySpec {

  "Validating a conversation" must {

    "be successful when optional fields are present" in {
      val conversationJson: JsValue = Resources.readJson("model/core/conversation.json")
      conversationJson.validate[Conversation] mustBe JsSuccess(
        ConversationUtil.getFullConversation("D-80542-20201120", "HMRC-CUS-ORG", "EORINumber", "GB1234567890"))
    }

    "be successful when optional fields are not present" in {
      val conversationJson: JsValue = Resources.readJson("model/core/conversation-minimal.json")
      conversationJson.validate[Conversation] mustBe JsSuccess(
        ConversationUtil.getMinimalConversation("D-80542-20201120"))
    }
  }

}
