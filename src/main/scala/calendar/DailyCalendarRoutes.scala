package calendar

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import calendar.DailyCalendarRegistry._
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout

class DailyCalendarRoutes(dailyCalendarRegistry: ActorRef[DailyCalendarRegistry.Command])(implicit val system: ActorSystem[_]) {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  // If ask takes more time than this to complete the request is failed
  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def createCalendar(events: Events): Future[CalendarCreatedResponse] =
    dailyCalendarRegistry.ask(CreateCalendar(events, _))

  def getCalendar(id: String): Future[GetCalendarResponse] =
    dailyCalendarRegistry.ask(GetCalendar(id, _))

  val eventRoutes: Route =
  pathPrefix("daily-calendars") {
    concat(
      pathEnd {
        concat(
          //#create-calendar
          post {
            entity(as[Events]) { events =>
              onSuccess(createCalendar(events)) { response =>
                complete((StatusCodes.Created, response.dailyCalendar))
              }
            }
          })

      },
      path(Segment) { id =>
        concat(
          //#get-calendar-info
          get {
            rejectEmptyResponse {
              onSuccess(getCalendar(id)) { response =>
                complete(response.dailyCalendar)
              }
            }
          })
      }
    )
  }
}
