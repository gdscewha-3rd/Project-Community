package com.example.leafy2.cardnews

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.leafy2.databinding.FragmentContentsBinding

class ContentsFragment : Fragment() {

    private lateinit var binding: FragmentContentsBinding
    private lateinit var mPager: ViewPager2
    private var idx: Int = 0

    companion object {
        const val INDEX = "index"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idx = it.getInt(INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPager = binding.pager
        mPager.setPageTransformer(ZoomOutPageTransformer())

        val mDataset = DataSource().loadContents(idx)
        val pagerAdapter = ContentsAdapter(mDataset)
        mPager.adapter = pagerAdapter
    }


}