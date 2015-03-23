package com.mycompany;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;
import sun.security.provider.certpath.Vertex;

/**
 * Created by artem on 3/18/15.
 */
public class AvgStatsCounter extends Verticle {

	private double average;
	private long time = -1L;
	private long messagesReceived;

	@Override
	public void start() {

		vertx.eventBus().registerHandler("data.source.raw", new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				Long envelopeTime = message.body().getLong("time");
				if (time == -1L) {
					time = envelopeTime;
				}
				if (time <= envelopeTime) {
					messagesReceived++;

					Long data = message.body().getLong("data");

					average = updateAverage(data);
				}
			}
		});

		vertx.setPeriodic(1000, new Handler<Long>() {
			@Override
			public void handle(Long event) {
				JsonObject envelope = new JsonObject()
						.putNumber("since", time)
						.putNumber("avg", average);
				vertx.eventBus().publish("data.source.average", envelope);
			}
		});


		container.logger().info("AvgStatsCounter started");

	}

	private double updateAverage(long newValue) {
		return 1.0 * average * messagesReceived / (messagesReceived + 1) + newValue / (messagesReceived + 1.0);
	}
}
