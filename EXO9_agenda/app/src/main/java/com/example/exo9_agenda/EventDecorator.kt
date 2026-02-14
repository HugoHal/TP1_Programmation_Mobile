import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventDecorator(private val color: Int, private val dates: Collection<CalendarDay>) : DayViewDecorator {

    // Est-ce qu'on doit décorer ce jour précis ?
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    // Si oui, qu'est-ce qu'on dessine ? (Ici un point de rayon 5 pixels)
    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(10f, color)) // 10f est la taille du point
    }
}