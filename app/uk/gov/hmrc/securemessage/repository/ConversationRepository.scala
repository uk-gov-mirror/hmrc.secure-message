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

package uk.gov.hmrc.securemessage.repository

import cats.implicits.catsSyntaxEq
import org.joda.time.DateTime
import play.api.libs.json.JodaWrites.{ JodaDateTimeWrites => _ }
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{ JsObject, JsString, Json }
import reactivemongo.api.Cursor
import reactivemongo.api.Cursor.ErrorHandler
import reactivemongo.api.indexes.{ Index, IndexType }
import reactivemongo.bson.BSONObjectID
import reactivemongo.core.errors.DatabaseException
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import uk.gov.hmrc.mongo.{ MongoConnector, ReactiveRepository }
import uk.gov.hmrc.securemessage.controllers.models.generic.CustomerEnrolment
import uk.gov.hmrc.securemessage.models.core.Message.dateFormat
import uk.gov.hmrc.securemessage.models.core.{ Conversation, Message, Participants }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }

@SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter", "org.wartremover.warts.Nothing"))
class ConversationRepository @Inject()(implicit connector: MongoConnector)
    extends ReactiveRepository[Conversation, BSONObjectID](
      "conversation",
      connector.db,
      Conversation.conversationFormat,
      ReactiveMongoFormats.objectIdFormats) {

  private val DuplicateKey = 11000

  override def indexes: Seq[Index] =
    Seq(
      Index(
        key = Seq("client" -> IndexType.Ascending, "conversationId" -> IndexType.Ascending),
        name = Some("unique-conversation"),
        unique = true,
        sparse = true))

  def insertIfUnique(conversation: Conversation)(implicit ec: ExecutionContext): Future[Boolean] =
    insert(conversation)
      .map(_ => true)
      .recoverWith {
        case e: DatabaseException if e.code.contains(DuplicateKey) =>
          logger.warn("Ignoring duplicate found on insertion to conversation collection: " + e.getMessage())
          Future.successful(false)
      }

  def getConversations(enrolment: CustomerEnrolment)(implicit ec: ExecutionContext): Future[List[Conversation]] = {
    import uk.gov.hmrc.securemessage.models.core.Conversation.conversationFormat
    collection
      .find[JsObject, Conversation](selector = Json.obj(findByEnrolmentQuery(enrolment): _*), None)
      .sort(Json.obj("_id" -> -1))
      .cursor[Conversation]()
      .collect[List](-1, Cursor.FailOnError[List[Conversation]]())
  }

  def addMessageToConversation(client: String, conversationId: String, message: Message)(
    implicit ec: ExecutionContext): Future[Unit] =
    findAndUpdate(
      query = Json.obj("client" -> client, "conversationId" -> conversationId),
      update = Json.obj("$push" -> Json.obj("messages" -> message))
    ).map(_ => ())

  def getConversation(client: String, conversationId: String, enrolment: CustomerEnrolment)(
    implicit ec: ExecutionContext): Future[Option[Conversation]] =
    collection
      .find[JsObject, Conversation](
        selector = Json.obj("client" -> client, "conversationId" -> conversationId)
          deepMerge Json.obj(findByEnrolmentQuery(enrolment): _*),
        None)
      .one[Conversation]

  def conversationExists(client: String, conversationId: String)(implicit ec: ExecutionContext): Future[Boolean] =
    count(query = Json.obj("client" -> client, "conversationId" -> conversationId)).flatMap { count =>
      if (count === 0) Future(false) else Future(true)
    }

  def getConversationParticipants(client: String, conversationId: String)(
    implicit ec: ExecutionContext): Future[Option[Participants]] =
    collection
      .find[JsObject, JsObject](
        selector = Json.obj("client" -> client, "conversationId" -> conversationId),
        projection = Some(Json.obj("participants" -> 1)))
      .one[Participants]

  def participantErrorHandler: ErrorHandler[Participants] = Cursor.FailOnError[Participants]()

  private def findByEnrolmentQuery(enrolment: CustomerEnrolment): Seq[(String, JsValueWrapper)] =
    Seq(
      "participants.identifier.name"      -> JsString(enrolment.name),
      "participants.identifier.value"     -> JsString(enrolment.value),
      "participants.identifier.enrolment" -> JsString(enrolment.key)
    )

  def updateConversationWithReadTime(client: String, conversationId: String, id: Int, readTime: DateTime)(
    implicit ec: ExecutionContext): Future[Boolean] =
    collection
      .update(ordered = false)
      .one(
        Json.obj("client" -> client, "conversationId" -> conversationId, "participants.id" -> id),
        Json.obj("$push"  -> Json.obj("participants.$.readTimes" -> readTime))
      )
      .map(_.ok)
}
