package com.meltingb.medicare.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.meltingb.medicare.R
import com.meltingb.medicare.api.Item
import com.meltingb.medicare.api.repo.ApiRepository
import com.meltingb.medicare.core.BaseFragment
import com.meltingb.medicare.databinding.FragmentSearchBinding
import com.meltingb.medicare.utils.NavigationEvent
import com.meltingb.medicare.utils.TYPE_CODE
import com.meltingb.medicare.utils.TYPE_NAME
import com.meltingb.medicare.utils.TYPE_SEQ
import com.meltingb.medicare.view.adapter.SearchListViewAdapter
import com.meltingb.medicare.view.viewmodel.SearchViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    val viewModel: SearchViewModel by viewModel()
    lateinit var selectedPill: Item
    lateinit var mAdView: AdView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        binding.spinnerType.setOnSpinnerItemSelectedListener<String> { _, _, newIndex, _ ->
            viewModel.searchTypeLiveData.value = newIndex
            when (newIndex) {
                TYPE_NAME -> viewModel.hintTextLiveData.value = "의약품목명 입력"
                TYPE_SEQ -> viewModel.hintTextLiveData.value = "의약품 일련번호 입력"
                TYPE_CODE -> viewModel.hintTextLiveData.value = "보험코드 입력"
            }
            viewModel.searchTextLiveData.value = ""
        }
        binding.spinnerType.selectItemByIndex(0)

        viewModel.searchListLiveData.observe(viewLifecycleOwner, {
            val adapter = SearchListViewAdapter(requireContext(), it)
            binding.rvPillInfo.adapter = adapter
            binding.rvPillInfo.layoutManager = LinearLayoutManager(requireContext())
            adapter.setItemClickListener(object : SearchListViewAdapter.RecyclerViewItemClickListener {
                override fun onClick(view: View, position: Int) {
                    viewModel.confirmDetails(it[position], view)
                }
            })
        })

        viewModel.navigationLiveData.observe(viewLifecycleOwner, ::navigate)
    }

    private fun navigate(event: NavigationEvent) {
        when (event) {
            NavigationEvent.HomeView -> findNavController().popBackStack()
            NavigationEvent.HelpView -> findNavController().navigate(R.id.action_searchFragment_to_takeDetailFragment)
            NavigationEvent.SearchView -> {
                hideKeyboard()
                removeFocus()
            }
            NavigationEvent.MapView -> findNavController().navigate(R.id.action_searchFragment_to_mapFragment)
            NavigationEvent.AddView -> findNavController().navigate(R.id.action_searchFragment_to_addFragment)
        }
    }
}