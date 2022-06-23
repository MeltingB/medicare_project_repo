package com.meltingb.medicare.view.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.meltingb.medicare.R
import com.meltingb.medicare.core.BaseFragment
import com.meltingb.medicare.databinding.FragmentHomeBinding
import com.meltingb.medicare.utils.Common
import com.meltingb.medicare.utils.NavigationEvent
import com.meltingb.medicare.view.adapter.AlarmListViewAdapter
import com.meltingb.medicare.view.viewmodel.HomeViewModel
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.*


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModel()

    private var selectedDate: LocalDate? = null
    private var prevDate: LocalDate? = null

    private val dateFormatter = DateTimeFormat.forPattern("dd")
    private val dayFormatter = DateTimeFormat.forPattern("E")
    private val monthFormatter = DateTimeFormat.forPattern("MMM")
    val dateFormat: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), ActivityResultCallback { isGranted ->
            if (isGranted) findNavController().navigate(R.id.action_homeFragment_to_mapFragment)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.navigationLiveData.observe(viewLifecycleOwner, ::navigate)
        viewModel.alarmListLiveData.observe(viewLifecycleOwner, {
            val selectedDateStr = dateFormat.print(DateTime(selectedDate.toString()))
            val adapter = AlarmListViewAdapter(it, selectedDateStr)
            binding.rvMedicine.adapter = adapter
            binding.rvMedicine.layoutManager = LinearLayoutManager(requireContext())

            adapter.setItemClickListener(object : AlarmListViewAdapter.RecyclerViewItemClickListener {
                override fun onClick(view: View, position: Int) {
                    val direction = HomeFragmentDirections.actionHomeFragmentToEditFragment(it[position])
                    findNavController().navigate(direction)
                }
            })

            adapter.setCheckboxChangeListener(object : AlarmListViewAdapter.CheckboxChangeListener {
                override fun onChanged(position: Int, isChecked: Boolean) {
                    val item = it[position]
                    if (item.takeDayList.contains(selectedDateStr)) {
                        val takeList = item.takeDayList.toMutableList()
                        takeList.remove(selectedDateStr)
                        viewModel.updateTakeList(takeList, item.pillID!!)
                    } else {
                        val takeList = item.takeDayList.toMutableList()
                        takeList.add(selectedDateStr)
                        viewModel.updateTakeList(takeList, item.pillID!!)
                    }
                }
            })
        })


        binding.calendarView.apply {
            val heightPx = 100.toPx(requireContext())
            daySize = CalendarView.sizeAutoWidth(heightPx)
        }

        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.bind(day)
            }
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val daysOfWeek = arrayOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY
        )

        binding.calendarView.setup(firstMonth, lastMonth, daysOfWeek.first())
        binding.calendarView.scrollToDate(LocalDate.now())
        selectedDate = LocalDate.now()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val monthText: TextView = view.findViewById(R.id.tv_month)
        val dateText: TextView = view.findViewById(R.id.tv_date)
        val dayText: TextView = view.findViewById(R.id.tv_day)
        lateinit var day: CalendarDay

        init {
            view.setOnClickListener {
                val firstDay = binding.calendarView.findFirstVisibleDay()
                val lastDay = binding.calendarView.findLastVisibleDay()

                if (firstDay == day) {
                    binding.calendarView.smoothScrollToDate(day.date)
                } else if (lastDay == day) {
                    binding.calendarView.smoothScrollToDate(day.date.minusDays(4))
                }
                if (selectedDate != day.date) {
                    val oldDate = selectedDate
                    selectedDate = day.date
                    binding.calendarView.notifyDateChanged(day.date)
                    oldDate?.let { binding.calendarView.notifyDateChanged(it) }
                }
            }
        }

        fun bind(day: CalendarDay) {
            this.day = day
            monthText.text = monthFormatter.withLocale(Locale.US).print(DateTime(day.date.toString()))
            dateText.text = dateFormatter.print(DateTime(day.date.toString()))
            dayText.text = dayFormatter.withLocale(Locale.US).print(DateTime(day.date.toString()))

            val dayWidth = binding.calendarView.daySize.width
            dateText.layoutParams = LinearLayout.LayoutParams(dayWidth, dayWidth)

            if (day.date == selectedDate) {
                dateText.setBackgroundResource(R.drawable.calendar_selected_bg)
                viewModel.getAlarmList(dateFormat.print(DateTime(day.date.toString())), day.date.dayOfWeek.value)
            } else {
                dateText.setBackgroundResource(R.color.col_transparency)
            }
        }
    }

    fun Int.toPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

    private fun navigate(event: NavigationEvent) {
        when (event) {
            NavigationEvent.HelpView -> findNavController().navigate(R.id.action_homeFragment_to_takeDetailFragment)
            NavigationEvent.SearchView -> findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            NavigationEvent.MapView -> {
                if (checkPermission()) {
                    findNavController().navigate(R.id.action_homeFragment_to_mapFragment)
                }
            }
            NavigationEvent.AddView -> findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                // 허용 거부
                Common.showRequestPermission(requireContext())
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            return false
        } else {
            return true
        }
    }

    companion object {
        const val REQUEST_CODE = 14
    }
}

