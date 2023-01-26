package calendar

import calendar.DailyCalendarRegistry.ActionPerformed
import spray.json.RootJsonFormat

import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val actionPerformedJsonFormat: RootJsonFormat[ActionPerformed] = jsonFormat1(ActionPerformed.apply)

  implicit val eventJsonFormat: RootJsonFormat[Event] = jsonFormat3(Event.apply)
  implicit val eventsJsonFormat: RootJsonFormat[Events] = jsonFormat1(Events.apply)
  implicit val positionJsonFormat: RootJsonFormat[Position] = jsonFormat3(Position.apply)
  implicit val eventViewJsonFormat: RootJsonFormat[EventView] = jsonFormat4(EventView.apply)
  implicit val dailyCalendarJsonFormat: RootJsonFormat[DailyCalendar] = jsonFormat2(DailyCalendar.apply)
}
