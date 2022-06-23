package com.meltingb.medicare.view.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.meltingb.medicare.R
import com.meltingb.medicare.api.Item
import com.meltingb.medicare.core.BaseFragment
import com.meltingb.medicare.data.Details
import com.meltingb.medicare.databinding.FragmentSearchDetailBinding
import com.meltingb.medicare.utils.warn
import com.meltingb.medicare.view.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchDetailFragment : BaseFragment<FragmentSearchDetailBinding>(R.layout.fragment_search_detail) {

    val viewModel: SearchViewModel by viewModel()
    val args: SearchDetailFragmentArgs by navArgs()
    lateinit var mAdView: AdView
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        viewModel.getDetailsBySeqNum(args.pillInfo.itemSeq!!, requireContext())

        viewModel.pillDetailsLiveData.observe(viewLifecycleOwner, { items ->
            if (items.isNotEmpty()) {
                initPillDetails(args.pillInfo, items.first())
            }
        })
    }

    private fun initPillDetails(info: Item, details: Details) {
        details.let {
            viewModel.pillEfcyLiveData.value =
                it.efcyQesitm?.replace("&lt;p&gt;", "")?.replace("&lt;/p&gt;", "\n")
            viewModel.pillUseLiveData.value =
                it.useMethodQesitm?.replace("&lt;p&gt;", "")?.replace("&lt;/p&gt;", "\n")
            viewModel.pillWarnLiveData.value =
                it.atpnWarnQesitm?.replace("&lt;p&gt;", "")?.replace("&lt;/p&gt;", "\n")
            viewModel.pillSideEffectLiveData.value =
                it.seQesitm?.replace("&lt;p&gt;", "")?.replace("&lt;/p&gt;", "\n")
            viewModel.pillDepositLiveData.value =
                it.depositMethodQesitm?.replace("&lt;p&gt;", "")?.replace("&lt;/p&gt;", "\n")
        }
        info.let {
            viewModel.pillNameLiveData.value = it.itemName
            viewModel.pillSeqNumLiveData.value = it.itemSeq
            viewModel.pillEnterpriseLiveData.value = it.entpName
            viewModel.pillEdiCodeLiveData.value = it.ediCode ?: "-"
            viewModel.pillShapeLiveData.value = it.frontCodeName
            viewModel.pillColorLiveData.value = it.drugShape + " / " + it.colorClass1 ?: "-"
            Glide.with(requireActivity())
                .load(it.itemImage)
                .into(binding.ivPill)
        }
    }


}