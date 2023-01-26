package calendar

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.collection.mutable.ListBuffer
import scala.collection.immutable
import io.jvm.uuid._


trait IEvent {
  val id: Int
  val start: Int
  val end: Int
}

final case class Event(id: Int, start: Int, end: Int) extends IEvent
final case class Events(events: List[Event])
final case class Position(left: Int, top: Int, width: Int)
final case class EventView(id: Int, start: Int, end: Int, position: Position) extends IEvent
final case class DailyCalendar(events: List[EventView], id: String)

final class Canva(height : Int, width : Int, padding : Int, maxEventsWidth : Int)

object Canva {
  val height: Int = 720
  val width: Int = 620
  val padding: Int = 10
  val maxEventsWidth: Int = 600
}

object DailyCalendarRegistry {
  // actor protocol
  sealed trait Command
  final case class CreateCalendar(events: Events, replyTo: ActorRef[CalendarCreatedResponse]) extends Command
  final case class GetCalendar(id: String, replyTo: ActorRef[GetCalendarResponse]) extends Command

  final case class CalendarCreatedResponse(dailyCalendar: DailyCalendar)
  final case class GetCalendarResponse(dailyCalendar: Option[DailyCalendar])
  final case class ActionPerformed(description: String)

  def apply(): Behavior[Command] = registry(Set.empty)

  private def registry(dailyCalendars: Set[DailyCalendar]): Behavior[Command] =
    Behaviors.receiveMessage {
      case CreateCalendar(requestEvents, replyTo) =>
        var processedEvents = new ListBuffer[EventView]()
        var orderedEvents = requestEvents.events.sortWith(_.start < _.start)

        var processedCollisions: List[(Int, Int)] = calculateCollisionsForEvents(orderedEvents)
        var eventIndex = 0

        for (event <- orderedEvents) {
          var width = Canva.maxEventsWidth / processedCollisions.apply(eventIndex)._1
          var left = Canva.padding + (processedCollisions.apply(eventIndex)._2 - 1) * width
          var position = new Position(left, event.start, width)
          var eventView = new EventView(event.id, event.start, event.end, position)
          processedEvents += eventView
          eventIndex += 1
        }

        var dailyCalendar = new DailyCalendar(processedEvents.toList, UUID.random.string)
        replyTo ! CalendarCreatedResponse(dailyCalendar)
        registry(dailyCalendars + dailyCalendar)

      case GetCalendar(id, replyTo) =>
        replyTo ! GetCalendarResponse(dailyCalendars.find(_.id == id))
        Behaviors.same
    }

  // @TODO description
  private def collectCollisions(orderedEvents: List[Event]) : List[Int] = {
    var elementsInContainer = 1
    var collisionsCounter = new ListBuffer[Int]()

    for (eventIndex <- 0 to (orderedEvents.size - 2)) {
      var currentEvent = orderedEvents.apply(eventIndex)
      var nextEvent = orderedEvents.apply(eventIndex + 1)

      if (currentEvent.end > nextEvent.start) {
        elementsInContainer += 1
      } else {
        collisionsCounter += elementsInContainer
        elementsInContainer = 1
      }
    }

    collisionsCounter += elementsInContainer

    return collisionsCounter.toList
  }

  // @TODO description
  private def calculateCollisionsForEvents(orderedEvents: List[Event]) : List[(Int, Int)] = {
    var collisionsCounter = collectCollisions(orderedEvents)
    var processedCollisions = new ListBuffer[(Int, Int)]

    for (elementsInContainer <- collisionsCounter) {
      for (orderInContainer <- 1 to elementsInContainer) {
        processedCollisions += ((elementsInContainer, orderInContainer))
      }
    }

    return processedCollisions.toList
  }
}