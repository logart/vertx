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
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.Random;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class DataProducer extends Verticle {

	public void start() {


		vertx.setPeriodic(10, new Handler<Long>() {
			@Override
			public void handle(Long timer) {
				vertx.eventBus().publish("data.source.raw", createPackage());
			}
		});

		container.logger().info("DataProducer started");

	}

	private JsonObject createPackage() {
		Random random = new Random();

		JsonObject result = new JsonObject()
				.putNumber("time", System.currentTimeMillis())
				.putNumber("data", random.nextInt(1000));


		return result;
	}
}
