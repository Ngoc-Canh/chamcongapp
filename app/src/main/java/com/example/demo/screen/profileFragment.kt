package com.example.demo.screen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.demo.R
import com.example.demo.backend.SessionManager

class profileFragment : Fragment() {
    private lateinit var btnLogOut: Button
    private lateinit var tvUserName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvEmailManager: TextView
    private lateinit var tvDayOff: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogOut = view.findViewById(R.id.buttonLogOut)
        tvUserName = view.findViewById(R.id.textView9)
        tvEmail = view.findViewById(R.id.textView10)
        tvEmailManager = view.findViewById(R.id.textView13)
        tvDayOff = view.findViewById(R.id.textView15)

        sessionManager = SessionManager(context?.applicationContext!!)

        btnLogOut.setOnClickListener {
            sessionManager.refreshAll()
            activity?.finish()
        }

        tvUserName.text = sessionManager.fetchUserName()
        tvEmail.text = sessionManager.fetchMyEmail()
        tvEmailManager.text = sessionManager.fetchManagerEmail()
        tvDayOff.text = sessionManager.fetchDayOff()

//        sessionManager = SessionManager(context!!)
//        events = ArrayList()
//        drw_dot_success = getImage(activity?.applicationContext!!,
//            "dot_span_success")?.alpha.toString()
//        drw_dot_error = getImage(activity?.applicationContext!!,
//            "dot_span_danger")?.alpha.toString()
//        drw_holiday = getImage(activity?.applicationContext!!,
//            "ic_baseline_beach_access_24")?.alpha.toString()
//
//        val materialCalendar = view.findViewById<CalendarView>(R.id.materialCalendar)
//
//        val eventsCheckIn = ArrayList<List<String>>()
//
//        val otherStrings = listOf("In&Out", "20", "5", "2021", "valid", "valid")
//        val otherStrings2 = listOf("In&Out", "21", "5", "2021", "valid", "invalid")
//        val otherStrings3 = listOf("", "22", "5", "2021", "", "")
//        val otherStrings4 = listOf("Holiday", "23", "5", "2021", "null", "null")
//        val otherStrings5 = listOf("Holiday", "31", "7", "2021", "null", "null")
//        val otherStrings6 = listOf("Holiday", "1", "8", "2021", "null", "null")
//
//        val sdf = SimpleDateFormat("yyyyMMdd")
//
//        eventsCheckIn.add(otherStrings)
//        eventsCheckIn.add(otherStrings2)
//        eventsCheckIn.add(otherStrings3)
//        eventsCheckIn.add(otherStrings4)
//        eventsCheckIn.add(otherStrings5)
//        eventsCheckIn.add(otherStrings5)
//        eventsCheckIn.add(otherStrings6)
//        eventsCheckIn.add(otherStrings6)
//
//        val request = ApiClient.getClient().create(RestAPI::class.java)
//
//        val call = request.history("Token ${sessionManager.fetchAuthToken()}")
//        call.enqueue(object: Callback<Event> {
//            override fun onResponse(call: Call<Event>, response: Response<Event>) {
//
//                try {
//                    if(response.code() == 200){
//                        val list: ArrayList<EventDetail>? = response.body()?.event
//                        if (list != null){
//                            dict = mutableMapOf()
//                            for (event in list){
//                                if (dict.containsKey("${event.created_date}")){
//                                    dict["${event.created_date!!}"]?.add(event)
//                                }else{
//                                    val array = ArrayList<EventDetail>()
//                                    array.add(event)
//                                    dict["${event.created_date!!}"] = array
//                                }
//                            }
//                        }
//
//                        for(key in dict.keys){
//                            val calendar = Calendar.getInstance()
//                            val dateFm = Date(key)
//                            calendar.time = dateFm
//
//                            if (dict[key]!!.size == 1){
//                                val data = getCheckInOut(dict[key]!![0])
//                                val dataSpl = data.split("_")
//
//                                if (dataSpl[0] == CHECK_IN){
//                                    createNoteCalendar(calendar, validCheckIn = dataSpl[1].toBoolean())
//                                }else{
//                                    createNoteCalendar(calendar, validCheckOut = dataSpl[1].toBoolean())
//                                }
//                            }else if (dict[key]!!.size == 2){
//                                val dataCheckIn = dict[key]!![0]
//                                val dataCheckOut = dict[key]!![1]
//
//                                createNoteCalendar(
//                                    calendar,
//                                    validCheckIn = dataCheckIn.valid.toBoolean(),
//                                    validCheckOut = dataCheckOut.valid.toBoolean()
//                                )
//                            }
//                        }
//                        materialCalendar.setEvents(events)
//                    }
//                } catch (ex:Exception){
//                    Log.e("History", ex.toString())
//                }
//
//            }
//
//            override fun onFailure(call: Call<Event>, t: Throwable) {
//                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
//                Log.i("History", t.message.toString())
//            }
//        })
//
//        eventsCheckIn.forEach lit@{
//            val calendar = Calendar.getInstance()
//            val dateEvent = sdf.parse("${it[3].toInt()} ${it[2].toInt()}${it[1].toInt()}")
//            calendar.time = dateEvent!!
//
//            when {
//                it[0] == "In&Out" -> {
//                    if (it[4] != "valid" || it[5] != "valid"){
//                        events.add(EventDay(calendar, R.drawable.dot_span_warning))
//                        return@lit
//                    }
//                    events.add(EventDay(calendar, R.drawable.dot_span_success))
//                }
//                it[0] == "" -> {
//                    events.add(EventDay(calendar, R.drawable.dot_span_danger))
//                }
//                it[0] == "Holiday" -> {
//                    events.add(EventDay(calendar, R.drawable.ic_baseline_beach_access_24))
//                }else -> {
//                    events.add(EventDay(calendar, R.drawable.dot_span_warning))
//                }
//            }
//        }
//        materialCalendar.setEvents(events)
//
//        materialCalendar.setOnPreviousPageChangeListener {
//            val strMonthAndYear = sessionManager.getMonthYearCalendar(-1)
//            println(strMonthAndYear)
//        }
//
//        materialCalendar.setOnForwardPageChangeListener {
//            val strMonthAndYear = sessionManager.getMonthYearCalendar(1)
//            println(strMonthAndYear)
//        }
//
//        materialCalendar.setOnDayClickListener {
//            events.forEach {
//                eventDay ->  if(eventDay.calendar == it.calendar){
//                    println(eventDay)
//                    getImage(activity?.applicationContext!!, "ic_baseline_beach_access_24")
//                }
//            }
//        }
    }

//    private fun createNoteCalendar(calendar: Calendar, validCheckIn: Boolean = false, validCheckOut: Boolean = false){
//        if(validCheckIn && validCheckOut){
//            events.add(EventDay(calendar, R.drawable.dot_span_success))
//        }else if (!validCheckIn || !validCheckOut){
//            events.add(EventDay(calendar, R.drawable.dot_span_danger))
//        }
//    }
//
//    private fun getCheckInOut(data: EventDetail): String{
//        return if (data.event_type == CHECK_IN){
//            "${data.event_type}_${data.valid}"
//        }else{
//            "${data.event_type}_${data.valid}"
//        }
//    }

//    private fun getImage(c: Context, imageName: String?): Drawable? {
//        return c.resources
//            .getDrawable(c.resources.getIdentifier(imageName, "drawable", c.packageName))
//    }
//
//    private fun compareIsHoliday(drw: Drawable): Boolean {
//        return drw.alpha.toString() == drw_holiday
//    }

    // ResourcesCompat.getDrawable(resources, (eventDay.imageDrawable as Int).toInt(), null)
}