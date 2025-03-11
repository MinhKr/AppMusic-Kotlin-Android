package com.minhldn.appmusic.presentation.fragment.explore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.minhldn.appmusic.data.network.ApiService
import com.minhldn.appmusic.databinding.FragmentExploreBinding
import com.minhldn.appmusic.presentation.adapter.SongAdapter
import com.minhldn.appmusic.presentation.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private val apiService = ApiService.create()

    private val viewModel: SongViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        viewModel.songs.observe(viewLifecycleOwner){
            binding.rvExplore.adapter = SongAdapter(it)
        }

        viewModel.fetchSongs()
    }

    private fun initView() {
        binding.rvExplore.let {
            it.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun fetchSongs() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val songs = withContext(Dispatchers.IO) {
                    apiService.getSongs()
                }
                binding.rvExplore.adapter = SongAdapter(songs)
            } catch (e: Exception) {
                Log.d("ExploreFragment", "Error fetching songs: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExploreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExploreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}