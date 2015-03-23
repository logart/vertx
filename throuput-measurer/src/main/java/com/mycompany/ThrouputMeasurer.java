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
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class ThrouputMeasurer extends Verticle {

	private long messagesCounter;
	private long startTime = -1L;

	public void start() {


		vertx.eventBus().registerHandler("data.source.raw", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				if (startTime == -1L) {
					startTime = System.currentTimeMillis();
					messagesCounter = 0;
				}
				messagesCounter++;
			}
		});

		vertx.setPeriodic(1000, new Handler<Long>() {
			@Override
			public void handle(Long event) {
				container.logger().debug(messagesCounter);
				long data = 1000 * messagesCounter / (System.currentTimeMillis() - startTime);
				vertx.eventBus().publish("data.source.messages.per.second", data);
			}
		});

		container.logger().info("ThrouputMeasurer started");

	}
}
