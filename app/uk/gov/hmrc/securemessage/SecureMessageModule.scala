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

package uk.gov.hmrc.securemessage

import com.google.inject.{ AbstractModule, Provides }
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.mongo.MongoConnector
import uk.gov.hmrc.time.DateTimeUtils

import javax.inject.Singleton

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class SecureMessageModule extends AbstractModule {

  @Provides
  @Singleton
  def mongoConnectorProvider(reactiveMongoComponent: ReactiveMongoComponent): MongoConnector =
    reactiveMongoComponent.mongoConnector

  override def configure(): Unit = {
    bind(classOf[DateTimeUtils]).to(classOf[TimeProvider])
    super.configure()
  }
}

class TimeProvider extends DateTimeUtils
