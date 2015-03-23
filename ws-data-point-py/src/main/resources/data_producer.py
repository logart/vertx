import vertx
import random as rnd
import time as time
from core.event_bus import EventBus

def create_package():
    result = {
        'time' : int(round(time.time() * 1000)),
        'data' : 200#rnd.randint(0,500),
    }

    return result;


def handler(tid):
    EventBus.publish("data.source.raw", create_package())

tid = vertx.set_periodic(1, handler=handler)

vertx.logger().info("DataProducer started")