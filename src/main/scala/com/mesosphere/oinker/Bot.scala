package com.mesosphere.oinker

import com.netaporter.uri.dsl._
import com.twitter.finagle.Service
import com.twitter.finagle.httpx.{RequestBuilder, Response, Request}
import com.twitter.io.Buf
import com.twitter.util.Future

class Bot(baseUrl: String, client: Service[Request, Response]) {

  def oink(handle: String, content: String): Future[Response] = {
    val req = buildOink(handle, content)
    client(req)
  }

  private def buildOink(handle: String, content: String): Request = {
    val form = "" ? ("oink[handle]" -> handle) & ("oink[content]" -> content)
    val formString = form.toString().substring(2)
    RequestBuilder()
      .url(s"$baseUrl/oinks")
      .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
      .buildPost(Buf.Utf8(formString))
  }
}

