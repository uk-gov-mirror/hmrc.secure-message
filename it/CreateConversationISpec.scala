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

import org.scalatest.DoNotDiscover
import play.api.http.Status.{ BAD_REQUEST, CREATED }
import play.api.http.{ ContentTypes, HeaderNames }
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.Helpers._

@DoNotDiscover
@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class CreateConversationISpec extends ISpec {

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

    "return CREATED when sent a conversation request with no email address and it is found in CDS" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val response =
        wsClient
          .url(resource("/secure-messaging/conversation/cdcm/D-80542-20201120"))
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(new File("./it/resources/create-conversation-no-email.json"))
          .futureValue
      response.status mustBe CREATED
    }

    "return BAD REQUEST when sent a conversation request with an invalid email address" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val response =
        wsClient
          .url(resource("/secure-messaging/conversation/cdcm/D-80542-20201120"))
          .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
          .put(new File("./it/resources/create-conversation-invalid-email.json"))
          .futureValue
      response.status mustBe BAD_REQUEST
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

    "return CONFLICT when a conversation with the given conversationId already exists" in {
      val wsClient = app.injector.instanceOf[WSClient]
      val _ = wsClient
        .url(resource("/secure-messaging/conversation/cdcm/D-80542-20201120"))
        .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
        .put(new File("./it/resources/create-conversation-minimal.json"))
        .futureValue
      val response = wsClient
        .url(resource("/secure-messaging/conversation/cdcm/D-80542-20201120"))
        .withHttpHeaders((HeaderNames.CONTENT_TYPE, ContentTypes.JSON))
        .put(new File("./it/resources/create-conversation-minimal.json"))
        .futureValue
      response.status mustBe CONFLICT
    }
  }

}
