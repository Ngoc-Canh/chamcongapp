package com.example.demo.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.applandeo.materialcalendarview.EventDay
import com.example.demo.Constant
import com.example.demo.R
import com.example.demo.backend.ApiClient
import com.example.demo.backend.RestAPI
import com.example.demo.backend.SessionManager
import com.example.demo.backend.entities.DayOffDetail
import com.example.demo.backend.entities.Event
import com.example.demo.backend.entities.EventDetail
import com.example.demo.backend.entities.HolidayDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val STATUS_WAITING = "waiting"
private const val STATUS_ACCEPT = "accept"
private const val STATUS_DECLINE = "decline"

/**
 * A simple [Fragment] subclass.
 * Use the [homeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class historyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var sessionManager: SessionManager
    private lateinit var dictEvent: MutableMap<String, ArrayList<EventDetail>>
    private lateinit var dictHoliday: MutableMap<String, ArrayList<HolidayDetail>>
    private lateinit var dictDayOff: MutableMap<String, ArrayList<DayOffDetail>>
    private lateinit var events: ArrayList<EventDay>
    private lateinit var materialCalendar: com.applandeo.materialcalendarview.CalendarView
    private lateinit var listData: ArrayList<String>
    private lateinit var linearCheckIn: LinearLayout
    private lateinit var linearCheckOut: LinearLayout
    private lateinit var linearReason: LinearLayout
    private lateinit var tv_checkIn: TextView
    private lateinit var tv_checkOut: TextView
    private lateinit var tv_reason: TextView
    private lateinit var swipeRefreshHistory: SwipeRefreshLayout
    private var callApi: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())
        events = ArrayList()
        listData = ArrayList()
        dictEvent = mutableMapOf()
        dictHoliday = mutableMapOf()
        dictDayOff = mutableMapOf()

        materialCalendar = view.findViewById(R.id.materialCalendarView)
        linearCheckIn = view.findViewById(R.id.linearCheckIn)
        linearCheckOut = view.findViewById(R.id.linearCheckOut)
        linearReason = view.findViewById(R.id.linearReason)
        tv_checkIn = view.findViewById(R.id.tv_checkIn)
        tv_checkOut = view.findViewById(R.id.tv_checkOut)
        tv_reason = view.findViewById(R.id.tv_reason)
        swipeRefreshHistory = view.findViewById(R.id.swipeRefreshHistory)

        swipeRefreshHistory.setOnRefreshListener {
            reloadData()
        }
        swipeRefreshHistory.isRefreshing = true

        materialCalendar.setOnPreviousPageChangeListener {
            swipeRefreshHistory.isRefreshing = true
            linearCheckIn.visibility = View.GONE
            linearCheckOut.visibility = View.GONE
            linearReason.visibility = View.GONE
            val monthAndYear = sessionManager.getMonthYearCalendar(-1).split("/")
            handleHistory(month = monthAndYear[0], year = monthAndYear[1])
        }

        materialCalendar.setOnForwardPageChangeListener {
            swipeRefreshHistory.isRefreshing = true
            linearCheckIn.visibility = View.GONE
            linearCheckOut.visibility = View.GONE
            linearReason.visibility = View.GONE
            val monthAndYear = sessionManager.getMonthYearCalendar(1).split("/")
            handleHistory(month = monthAndYear[0], year = monthAndYear[1])
        }

        materialCalendar.setOnDayClickListener {
            val itMonth = it.calendar.time.month
            val itDate = it.calendar.time.date

            dictEvent.forEach { eventDay ->
                val evt = Date(eventDay.key)
                if (evt.date == itDate && evt.month == itMonth) {
                    linearReason.visibility = View.GONE
                    linearCheckIn.visibility = View.VISIBLE
                    linearCheckOut.visibility = View.VISIBLE

                    try {
                        tv_checkIn.text = "Lúc đến: ${
                            sessionManager.convertTimeStampToTime(
                                timestamp = eventDay.value[0].created_at!!.toLong(),
                                "HH:mm"
                            )
                        }"
                        tv_checkOut.text = "Lúc Về: ${
                            sessionManager.convertTimeStampToTime(
                                timestamp = eventDay.value[1].created_at!!.toLong(),
                                "HH:mm"
                            )
                        }"
                    } catch (ex: Exception) {
                        tv_checkOut.text = ""
                    }
                    return@setOnDayClickListener
                }
            }

            dictHoliday.forEach { holiday ->
                val hld = Date(holiday.key)
                if (hld.date == itDate && hld.month == itMonth) {
                    linearCheckIn.visibility = View.GONE
                    linearCheckOut.visibility = View.GONE
                    linearReason.visibility = View.VISIBLE

                    tv_reason.text = holiday.value[0].description
                    return@setOnDayClickListener
                }
            }

            dictDayOff.forEach { dayOff ->
                val d_off = Date(dayOff.key)
                if (d_off.date == itDate && d_off.month == itMonth) {
                    linearCheckIn.visibility = View.GONE
                    linearCheckOut.visibility = View.GONE
                    linearReason.visibility = View.VISIBLE
                    val txt = if (dayOff.value[0].all_day == true) {
                        "cả ngày"
                    } else if (dayOff.value[0].morning == true) {
                        "vào ca sáng"
                    } else {
                        "vào ca chiều"
                    }
                    tv_reason.text = "${dayOff.value[0].type} $txt \nTrạng thái: ${dayOff.value[0].status}"
                    return@setOnDayClickListener
                }
            }

            linearCheckIn.visibility = View.GONE
            linearCheckOut.visibility = View.GONE
            linearReason.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    private fun handleHistory(month: String, year: String) {
        if (listData.contains("$month/$year")) {
            swipeRefreshHistory.isRefreshing = false
            return
        }
        if (!callApi) {
            return
        }
        val request = ApiClient.getClient().create(RestAPI::class.java)
        val call = request.history(
            "Token ${sessionManager.fetchAuthToken()}",
            month = month, year = year
        )
        call.enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                try {
                    if (response.code() == 200) {
                        listData.add("$month/$year")
                        val list: ArrayList<EventDetail>? = response.body()?.event
                        val listHoliday: ArrayList<HolidayDetail>? = response.body()?.holiday
                        val listDayOff: ArrayList<DayOffDetail>? = response.body()?.day_off

                        if (listHoliday != null) {
                            handleHoliday(listHoliday)
                        }

                        if (listDayOff != null) {
                            handleDayOff(listDayOff)
                        }

                        if (list != null) {
                            for (event in list) {
                                if (dictEvent.containsKey("${event.created_date}")) {
                                    dictEvent["${event.created_date!!}"]?.add(event)
                                } else {
                                    val array = ArrayList<EventDetail>()
                                    array.add(event)
                                    dictEvent["${event.created_date!!}"] = array
                                }
                            }
                            handleEvent()
                        }
                        swipeRefreshHistory.isRefreshing = false
                    }
                } catch (ex: Exception) {
                    swipeRefreshHistory.isRefreshing = false
                    Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {
                swipeRefreshHistory.isRefreshing = false
                Constant.dialogError(activity!!, "Có lỗi xảy ra vui lòng thử lại.")
            }
        })
    }

    private fun handleEvent() {
        for (key in dictEvent.keys) {
            val calendar = Calendar.getInstance()
            val dateFm = Date(key)
            calendar.time = dateFm

            if (dictEvent[key]!!.size == 1) {
                val data = getCheckInOut(dictEvent[key]!![0])
                val dataSpl = data.split("_")

                if (dataSpl[0] == Constant.CHECK_IN) {
                    createNoteCalendar(calendar, validCheckIn = dataSpl[1].toBoolean())
                } else {
                    createNoteCalendar(calendar, validCheckOut = dataSpl[1].toBoolean())
                }
            } else if (dictEvent[key]!!.size == 2) {
                val dataCheckIn = dictEvent[key]!![0]
                val dataCheckOut = dictEvent[key]!![1]

                createNoteCalendar(
                    calendar,
                    validCheckIn = dataCheckIn.valid.toBoolean(),
                    validCheckOut = dataCheckOut.valid.toBoolean()
                )
            }
        }
        materialCalendar.setEvents(events)
    }

    private fun handleHoliday(data: ArrayList<HolidayDetail>) {
        for (hld in data) {
            if (dictHoliday.containsKey("${hld.holiday}")) {
                dictHoliday["${hld.holiday}"]?.add(hld)
            } else {
                val array = ArrayList<HolidayDetail>()
                array.add(hld)
                dictHoliday["${hld.holiday}"] = array
            }
        }

        for (key in dictHoliday.keys) {
            val calendar = Calendar.getInstance()
            val dateFm = Date(key)
            calendar.time = dateFm

            events.add(EventDay(calendar, R.drawable.download))
        }
        materialCalendar.setEvents(events)
    }

    private fun handleDayOff(listDayOff: ArrayList<DayOffDetail>?) {
        if (listDayOff != null) {
            for (doff in listDayOff) {
                if (dictDayOff.containsKey("${doff.date}")) {
                    dictDayOff["${doff.date}"]?.add(doff)
                } else {
                    val array = ArrayList<DayOffDetail>()
                    array.add(doff)
                    dictDayOff["${doff.date}"] = array
                }
            }
        }

        for (key in dictDayOff.keys) {
            val calendar = Calendar.getInstance()
            val dateFm = Date(key)
            calendar.time = dateFm

            events.add(
                EventDay(
                    calendar,
                    R.drawable.day_off_sign_door_chain_isolated_white_background_d_rendered_113379702
                )
            )
        }
        materialCalendar.setEvents(events)
    }

    private fun createNoteCalendar(
        calendar: Calendar,
        validCheckIn: Boolean = false,
        validCheckOut: Boolean = false
    ) {
        if (validCheckIn && validCheckOut) {
            events.add(EventDay(calendar, R.drawable.dot_span_success))
        } else if (!validCheckIn || !validCheckOut) {
            events.add(EventDay(calendar, R.drawable.dot_span_danger))
        }
    }

    private fun getCheckInOut(data: EventDetail): String {
        return if (data.event_type == Constant.CHECK_IN) {
            "${data.event_type}_${data.valid}"
        } else {
            "${data.event_type}_${data.valid}"
        }
    }

    fun reloadData() {
        val calendar = Calendar.getInstance()
        materialCalendar.setDate(calendar)
        callApi = true
        events = ArrayList()
        listData = ArrayList()
        dictEvent = mutableMapOf()
        dictHoliday = mutableMapOf()
        dictDayOff = mutableMapOf()
        linearCheckIn.visibility = View.GONE
        linearCheckOut.visibility = View.GONE
        linearReason.visibility = View.GONE
        sessionManager.refreshMonthCalendar()

        val strMonthAndYear = sessionManager.getMonthYearCalendar(0).split("/")
        handleHistory(month = strMonthAndYear[0], year = strMonthAndYear[1])
    }

    override fun onDestroy() {
        super.onDestroy()
        sessionManager.refreshMonthCalendar()
    }
}