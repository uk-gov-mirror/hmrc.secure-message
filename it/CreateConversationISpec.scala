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

import java.io.File

import org.scalatestplus.play.PlaySpec
import play.api.http.Status._
import play.api.http.{ ContentTypes, HeaderNames }
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import uk.gov.hmrc.integration.ServiceSpec

class CreateConversationISpec extends PlaySpec with ServiceSpec {

  override def externalServices: Seq[String] = Seq.empty

  "A PUT request to /secure-messaging/conversation/{client}/{conversationId}" should {

    "return CREATED when sent a full and valid JSON payload" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val response =
        wsClient
          .url(resource("/secure-messaging/conversation/cdcm/D-80542-20201120"))
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(new File("./it/resources/create-conversation-full.json"))
          .futureValue
      response.status mustBe CREATED

    }

    "return CREATED when sent a minimal and valid JSON payload" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val response =
        wsClient
          .url(resource("/secure-messaging/conversation/cdcm/D-80542-20201120"))
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(new File("./it/resources/create-conversation-minimal.json"))
          .futureValue
      response.status mustBe CREATED
    }

    "return BAD REQUEST when sent a minimal and invalid JSON payload" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val response =
        wsClient
          .url(resource("/secure-messaging/conversation/cdcm/D-80542-20201120"))
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(Json.parse("""{"missing":"data"}""".stripMargin))
          .futureValue
      response.status mustBe BAD_REQUEST
    }
  }
}
