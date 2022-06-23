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
import com.meltingb.medicare.databinding.FragmentAddBinding
import com.meltingb.medicare.utils.NavigationEvent
import com.meltingb.medicare.view.PillSliderAdapter
import com.meltingb.medicare.view.viewmodel.AddViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddFragment : BaseFragment<FragmentAddBinding>(R.layout.fragment_add) {

    private val viewModel: AddViewModel by viewModel()
    private val args: AddFragmentArgs by navArgs()
    private var mInterstitialAd: InterstitialAd? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initAdvertisement()
        viewModel.navigationLiveData.observe(viewLifecycleOwner, {
            if (it == NavigationEvent.HomeView) {
                showAdvertisement()
            }
        })

        val iconList = listOf(R.drawable.ic_pill1, R.drawable.ic_pill2, R.drawable.ic_vitamin)
        val adapter = PillSliderAdapter(requireActivity(), iconList)
        binding.imageSlider.setSliderAdapter(adapter)
        binding.imageSlider.setCurrentPageListener {
            viewModel.pillImgNumLiveData.value = it
        }

        initDaysBtn()

        binding.spinnerTakeNum.setItems(R.array.item_take_num)
        binding.spinnerTakeNum.setOnSpinnerItemSelectedListener<String> { _, _, _, newItem ->
            viewModel.takeNumLiveData.value = newItem
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
        binding.imageSlider.currentPagePosition = pill.pillImageNum
        viewModel.pillNameLiveData.value = pill.pillName
        viewModel.takeNumLiveData.value = pill.takeNum
        viewModel.takeTypeLiveData.value = pill.takeType
        val alarmWeek = mutableListOf<Boolean>()
        pill.alarmWeek.forEach {
            alarmWeek.add(!it)
        }
        viewModel.alarmDayListLiveData.value = alarmWeek
        // 알람 시간 >> 디자인 변경 후 작업 필
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
                mInterstitialAd = null
            }
        }
    }


}