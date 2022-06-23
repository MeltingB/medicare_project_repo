package com.meltingb.medicare.view.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.marginStart
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import com.meltingb.medicare.R
import com.meltingb.medicare.core.BaseFragment
import com.meltingb.medicare.data.PillEntity
import com.meltingb.medicare.databinding.FragmentTakeDetailBinding
import com.meltingb.medicare.utils.NavigationEvent
import com.meltingb.medicare.view.adapter.TakeListViewAdapter
import com.meltingb.medicare.view.viewmodel.TakeDetailViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class TakeDetailFragment : BaseFragment<FragmentTakeDetailBinding>(R.layout.fragment_take_detail) {

    private val viewModel: TakeDetailViewModel by viewModel()
    private var selectedDay: LocalDate? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")
    private var takeDayList: MutableList<LocalDate> = mutableListOf()
    val dateFormat: org.joda.time.format.DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // 전체 상세 보기
        viewModel.getAllPill()
        viewModel.alarmListLiveData.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                initCalendar()
            }
        })

        viewModel.navigationLiveData.observe(viewLifecycleOwner, ::navigate)


        viewModel.pillInfoLiveData.observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.pillInfoTextLiveData.value = "${it.pillName} / ${it.takeNum}${it.takeType}"

                var takeDays = ""
                val daysArray = listOf("일", ",월", ",화", ",수", ",목", ",금", ",토")
                for (i in 0..6) {
                    if (it.alarmWeek[i]) takeDays += daysArray[i]
                }

                viewModel.pillAlarmTextLiveData.value = "$takeDays / ${it.alarmTime}"
                viewModel.pillStartDtTextLiveData.value = it.createAt
            }
        })

        viewModel.takeDaysLiveData.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                it.forEach { date ->
                    val day = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
                    takeDayList.add(day)
                }
//                initCalendar()
            }
        })

    }

    private fun navigate(event: NavigationEvent) {
        when (event) {
            NavigationEvent.HomeView -> findNavController().popBackStack()
            NavigationEvent.SearchView -> findNavController().navigate(R.id.action_takeDetailFragment_to_searchFragment)
            NavigationEvent.MapView -> findNavController().navigate(R.id.action_takeDetailFragment_to_mapFragment)
            NavigationEvent.AddView -> findNavController().navigate(R.id.action_takeDetailFragment_to_addFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initCalendar() {
        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            @SuppressLint("ResourceAsColor")
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
                val today = LocalDate.now()
                container.day = day
                val colorArray = listOf<Int>(resources.getColor(R.color.col_set1),
                    resources.getColor(R.color.col_set2), resources.getColor(R.color.col_set3),
                    resources.getColor(R.color.col_set4), resources.getColor(R.color.col_set5))
                var index = 0
                container.gridView.removeAllViews()
                val list = viewModel.alarmListLiveData.value
                val takeMap = list!!.groupBy { it.pillGroupID }
                var takeList = mutableListOf<PillEntity>()
                takeMap.values.forEach {
                    it.forEach { pill ->
                        pill.takeDayList.forEach {
                            val takeDay = LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
                            if (day.date == takeDay) {
                                takeList.add(pill)
                                val childView = View(requireContext())
                                val lp = LinearLayout.LayoutParams(10, 10)
                                lp.setMargins(3, 0, 3, 3)
                                childView.layoutParams = lp
                                childView.setBackgroundColor(colorArray[index % colorArray.size])
                                container.gridView.addView(childView)
                            }
                        }
                        index ++
                    }
                }
                container.bgView.setBackgroundColor(Color.parseColor("#ffffff"))
                if (day.owner == DayOwner.THIS_MONTH) {
                    if (selectedDay == day.date) {
                        container.textView.setTextColor(Color.parseColor("#ffffff"))
                        container.bgView.setBackgroundResource(R.drawable.calendar_selected_bg)
                        setDataSelectedDay(takeList)
                    } else if (today == day.date) {
                        container.textView.setTextColor(Color.parseColor("#477B72"))
                    } else {
                        container.bgView.setBackgroundColor(Color.parseColor("#ffffff"))
                        container.textView.setTextColor(Color.parseColor("#333333"))
                    }
                } else {
                    container.bgView.setBackgroundColor(Color.parseColor("#ffffff"))
                    container.textView.setTextColor(Color.parseColor("#999999"))
                }

            }
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)
        binding.calendarView.scrollToDate(LocalDate.now())
        selectedDay = LocalDate.now()

        binding.calendarView.monthScrollListener = {
            if (binding.calendarView.maxRowCount == 6) {
                binding.exOneYearText.text = it.yearMonth.year.toString()
                binding.exOneMonthText.text = monthTitleFormatter.format(it.yearMonth)
            } else {
                val firstDate = it.weekDays.first().first().date
                val lastDate = it.weekDays.last().last().date
                if (firstDate.yearMonth == lastDate.yearMonth) {
                    binding.exOneYearText.text = firstDate.yearMonth.year.toString()
                    binding.exOneMonthText.text = monthTitleFormatter.format(firstDate)
                } else {
                    binding.exOneMonthText.text =
                        "${monthTitleFormatter.format(firstDate)} - ${monthTitleFormatter.format(lastDate)}"
                    if (firstDate.year == lastDate.year) {
                        binding.exOneYearText.text = firstDate.yearMonth.year.toString()
                    } else {
                        binding.exOneYearText.text = "${firstDate.yearMonth.year} - ${lastDate.yearMonth.year}"
                    }
                }
            }
        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.tv_date)
        val bgView: LinearLayout = view.findViewById(R.id.background_view)

        //        val takeView: View = view.findViewById(R.id.take_view)
        val gridView: GridLayout = view.findViewById(R.id.gridView)
        lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                if (day.owner == DayOwner.PREVIOUS_MONTH) {
                    binding.calendarView.smoothScrollToDate(day.date)
                } else if (day.owner == DayOwner.NEXT_MONTH) {
                    binding.calendarView.smoothScrollToDate(day.date.plusDays(7))
                }
                if (selectedDay != day.date) {
                    val oldDate = selectedDay
                    oldDate?.let { binding.calendarView.notifyDateChanged(it) }
                    selectedDay = day.date
                    viewModel.getAlarmList(dateFormat.print(DateTime(selectedDay.toString())))
                    binding.calendarView.notifyDateChanged(day.date)
                }
            }
        }
    }

    private fun setDataSelectedDay(list: List<PillEntity>) {
        val adapter = TakeListViewAdapter(list)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

}