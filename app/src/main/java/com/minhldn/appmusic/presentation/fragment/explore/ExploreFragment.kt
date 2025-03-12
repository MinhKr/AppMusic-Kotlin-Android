package com.minhldn.appmusic.presentation.fragment.explore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.minhldn.appmusic.data.model.Song
import com.minhldn.appmusic.databinding.FragmentExploreBinding
import com.minhldn.appmusic.presentation.DetailActivity
import com.minhldn.appmusic.presentation.adapter.OnSongClickListener
import com.minhldn.appmusic.presentation.adapter.SongAdapter
import com.minhldn.appmusic.presentation.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class ExploreFragment : Fragment(), OnSongClickListener {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    /*private val apiService = ApiService.create()*/

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

        viewModel.songs.observe(viewLifecycleOwner) {
            binding.rvExplore.adapter = SongAdapter(it, this@ExploreFragment)
        }

        viewModel.fetchSongs()
    }

    private fun initView() {
        binding.rvExplore.let {
            it.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    /*private fun fetchSongs() {
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
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSongItemClick(song: Song) {
        val intent = Intent(requireContext(), DetailActivity::class.java)
        intent.putExtra("song", song)
        startActivity(intent)
    }
}