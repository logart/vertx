import vertx
import random as rnd
from core.event_bus import EventBus

def create_package():
    result = {
        'time' : int(round(time.time() * 1000)),
        'data' : 100#rnd.randint(0,100),
    }

    return result;


def handler(tid):
    EventBus.publish("data.source.raw", create_package())

    print 'And every second this is printed'

tid = vertx.set_periodic(1, handler)

vertx.logger.info("DataProducer started");
