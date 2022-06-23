package com.meltingb.medicare.view.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.meltingb.medicare.R
import com.meltingb.medicare.core.BaseFragment
import com.meltingb.medicare.data.PillEntity
import com.meltingb.medicare.databinding.FragmentEditBinding
import com.meltingb.medicare.utils.NavigationEvent
import com.meltingb.medicare.view.PillSliderAdapter
import com.meltingb.medicare.view.viewmodel.EditViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditFragment : BaseFragment<FragmentEditBinding>(R.layout.fragment_edit) {

    private val viewModel: EditViewModel by viewModel()
    private val args: AddFragmentArgs by navArgs()
    private var mInterstitialAd: InterstitialAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initAdvertisement()
        initDaysBtn()
        viewModel.navigationLiveData.observe(viewLifecycleOwner, {
            if (it == NavigationEvent.HomeView) {
                findNavController().popBackStack()
            }
        })

        val iconList = listOf(R.drawable.ic_pill1, R.drawable.ic_pill2, R.drawable.ic_vitamin)
        val adapter = PillSliderAdapter(requireActivity(), iconList)
        binding.imageSlider.setSliderAdapter(adapter)
        binding.imageSlider.setCurrentPageListener {
            viewModel.pillImgNumLiveData.value = it
        }

        initPillEntity(args.pillEntity!!)


        binding.spinnerTakeNum.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            viewModel.takeNumLiveData.value = newItem
        }
        binding.spinnerTakeType.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            viewModel.takeTypeLiveData.value = newItem
        }

    }

    override fun onResume() {
        super.onResume()
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), getString(R.string.ad_front_test_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }

    private fun initPillEntity(pill: PillEntity) {
        viewModel.pillIDLiveData.value = pill.pillID
        viewModel.groupIDLiveData.value = pill.pillGroupID
        binding.imageSlider.currentPagePosition = pill.pillImageNum
        binding.imageSlider.sliderAdapter.notifyDataSetChanged()
        viewModel.pillNameLiveData.value = pill.pillName
        viewModel.takeNumLiveData.value = pill.takeNum
        viewModel.takeTypeLiveData.value = pill.takeType
        viewModel.alarmIDLiveData.value = pill.alarmId
        setAlarmWeek(pill.alarmWeek)
//        val alarmWeek = mutableListOf<Boolean>()
//        pill.alarmWeek.forEach {
//            alarmWeek.add(!it)
//        }
//        viewModel.alarmDayListLiveData.value = alarmWeek
        viewModel.alarmTime1Visible.value = View.VISIBLE
        viewModel.timeHours1LiveData.value = pill.alarmTime.substring(0..1)
        viewModel.timeMinutes1LiveData.value = pill.alarmTime.substring(3..4)
        // 알람 시간 >> 디자인 변경 후 작업 필
    }

    private fun setAlarmWeek(list: List<Boolean>) {
        viewModel.sunDayChecked.value = list[0]
        binding.btnSun.isChecked = !list[0]
        viewModel.monDayChecked.value = list[1]
        binding.btnMon.isChecked = !list[1]
        viewModel.tueDayChecked.value = list[2]
        binding.btnThu.isChecked = !list[2]
        viewModel.wedDayChecked.value = list[3]
        binding.btnWed.isChecked = !list[3]
        viewModel.thuDayChecked.value = list[4]
        binding.btnThu.isChecked = !list[4]
        viewModel.friDayChecked.value = list[5]
        binding.btnFri.isChecked = !list[5]
        viewModel.satDayChecked.value = list[6]
        binding.btnSat.isChecked = !list[6]
    }

    private fun initDaysBtn() {
        binding.btnSun.setOnClickListener {
            viewModel.sunDayChecked.value = !binding.btnSun.isChecked
        }
        binding.btnMon.setOnClickListener {
            viewModel.monDayChecked.value = !binding.btnMon.isChecked
        }
        binding.btnTue.setOnClickListener {
            viewModel.tueDayChecked.value = !binding.btnTue.isChecked
        }
        binding.btnWed.setOnClickListener {
            viewModel.wedDayChecked.value = !binding.btnWed.isChecked
        }
        binding.btnThu.setOnClickListener {
            viewModel.thuDayChecked.value = !binding.btnThu.isChecked
        }
        binding.btnFri.setOnClickListener {
            viewModel.friDayChecked.value = !binding.btnFri.isChecked
        }
        binding.btnSat.setOnClickListener {
            viewModel.satDayChecked.value = !binding.btnSat.isChecked
        }
    }

    private fun initAdvertisement() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireContext(), getString(R.string.ad_front_test_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }

    private fun showAdvertisement() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(requireActivity())
        } else { // 광고 불러오기 실패 시 이동
            findNavController().popBackStack()
        }
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                findNavController().popBackStack()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                findNavController().popBackStack()
            }

            override fun onAdShowedFullScreenContent() {
                findNavController().popBackStack()
            }
        }
    }


}