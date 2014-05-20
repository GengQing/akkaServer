package com.good.akkaserver.eventbus;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.japi.LookupEventBus;

public class AkkaEventBusExample {
	public class Event {
		private String channel;

		public Event(final String channel) {
			this.channel = channel;
		}

		public String getChannel() {
			return channel;
		}
		
		@Override
		public String toString() {
			return channel;
		}
	}

	public class SomethingElseHappenedEvent extends Event {

		public SomethingElseHappenedEvent(String channel) {
			super(channel);
		}
	}

	// E, S, C
	public class EventBus1 extends LookupEventBus<Event, ActorRef, String> {



		@Override
		public int mapSize() {
			return 5;
		}


		@Override
		public String classify(Event arg0) {
			return arg0.getChannel();
		}


		@Override
		public int compareSubscribers(ActorRef arg0, ActorRef arg1) {
			return arg0.compareTo(arg1);
		}

		@Override
		public void publish(Event arg0, ActorRef arg1) {
			 arg1.tell(arg0, ActorRef.noSender());
			
		}
	}

	public static class EventHandler extends UntypedActor {

		@Override
		public void onReceive(Object arg0) throws Exception {
			System.out.println("event: " + arg0 + " \nthread :"
					+ Thread.currentThread().getName());
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new AkkaEventBusExample().createBusAndPublishEvents();
	}

	private void createBusAndPublishEvents() throws InterruptedException {
		final EventBus1 eventBus = new EventBus1();
		final ActorSystem actorSystem = ActorSystem.create("Events");
		final ActorRef actor = actorSystem
				.actorOf(Props.create(EventHandler.class));
		final ActorRef actor2 = actorSystem.actorOf(Props.create(EventHandler.class));
	      String CHANNEL1 = "channel1";
	      String CHANNEL2 = "channel2";
	      eventBus.subscribe(actor, CHANNEL1);
	      eventBus.subscribe(actor2, CHANNEL2);
	      
	      // Publish to CHANNEL1
	      eventBus.publish(new SomethingElseHappenedEvent(CHANNEL1));
	 
	      // Publish to CHANNEL2
	      eventBus.publish(new SomethingElseHappenedEvent(CHANNEL2));
	      
	      Thread.sleep(5000);
	      
	      actorSystem.shutdown();
	      
	      
	}

}
