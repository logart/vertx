package com.mycompany;
/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.streams.Pump;
import org.vertx.java.platform.Verticle;

import java.util.Random;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class WebUI extends Verticle {

	public void start() {

		vertx.createHttpServer()
				.requestHandler(new Handler<HttpServerRequest>() {
					@Override
					public void handle(HttpServerRequest request) {
						container.logger().info("path is " + request.path());
						if ("/".equalsIgnoreCase(request.path())) {
							request.response().sendFile("web/index.html");
						} else {
							request.response().sendFile("web" + request.path());
						}
					}
				})
				.websocketHandler(new Handler<ServerWebSocket>() {
					@Override
					public void handle(final ServerWebSocket ws) {


						if ("/data/raw".equalsIgnoreCase(ws.path())) {
							container.logger().info("------------------------------raw------------------------------------");
							container.logger().info(ws.localAddress());
							container.logger().info(ws.path());
							container.logger().info(ws.remoteAddress());
							vertx.eventBus().registerHandler("data.source.raw", new Handler<Message<JsonObject>>() {
								public Long lastTime = -1L;

								@Override
								public void handle(Message<JsonObject> message) {
									Long time = message.body().getLong("time");
									if (time > lastTime) {
										lastTime = time + 75;
										ws.write(createPackage(message.body()));
									}
								}
							});
						}

						if ("/data/average".equalsIgnoreCase(ws.path())) {
							container.logger().info("------------------------------avg------------------------------------");
							container.logger().info(ws.localAddress());
							container.logger().info(ws.path());
							container.logger().info(ws.remoteAddress());

							vertx.eventBus().registerHandler("data.source.average", new Handler<Message<JsonObject>>() {
								@Override
								public void handle(Message<JsonObject> message) {
									Number data = message.body().getNumber("avg");
									ws.write(new Buffer(String.valueOf(data)));
								}
							});
						}

						if ("/data/perf".equalsIgnoreCase(ws.path())) {
							container.logger().info("------------------------------perf-----------------------------------");
							container.logger().info(ws.localAddress());
							container.logger().info(ws.path());
							container.logger().info(ws.remoteAddress());

							vertx.eventBus().registerHandler("data.source.messages.per.second", new Handler<Message<Long>>() {
								@Override
								public void handle(Message<Long> message) {
									ws.write(new Buffer(String.valueOf(message.body())));
								}
							});
						}
					}
				})
				.listen(8080);


		container.logger().info("WebUI started");
	}


	private Buffer createPackage(JsonObject message) {
		String content = String.valueOf(message.getLong("time")) + ';' + message.getLong("data");

		return new Buffer(content);
	}
}
