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

package uk.gov.hmrc.securemessage.controllers.model.common

import play.api.libs.json.{ Json, Reads }
import uk.gov.hmrc.securemessage.models.core.Identifier

/** [[CustomerEnrolment]] along with [[SystemIdentifier]] are identifiers, probably should make part of common model
  * */
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
