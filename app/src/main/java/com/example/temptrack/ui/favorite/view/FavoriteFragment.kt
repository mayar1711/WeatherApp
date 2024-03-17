package com.example.temptrack.ui.favorite.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.temptrack.R
import com.example.temptrack.data.database.DatabaseClient
import com.example.temptrack.data.database.FavoriteLocalDataSourceImo
import com.example.temptrack.data.model.TempData
import com.example.temptrack.data.network.RetrofitClient
import com.example.temptrack.data.network.datasource.WeatherRemoteDataSourceImpl
import com.example.temptrack.data.repositry.WeatherRepositoryImpl
import com.example.temptrack.databinding.FragmentFavoriteBinding
import com.example.temptrack.ui.favorite.viewmodel.FavoriteViewModel
import com.example.temptrack.ui.favorite.viewmodel.FavoriteViewModelFactory
import com.example.temptrack.ui.home.view.HomeFragment
import com.example.temptrack.ui.map.MapsActivity
import com.example.temptrack.util.ResultCallBack
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: FavoriteListAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var viewModel: FavoriteViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding=FragmentFavoriteBinding.inflate(inflater,container,false)

         binding.recyclerFavorite.layoutManager = LinearLayoutManager(requireContext())
        adapter = FavoriteListAdapter(requireContext(),
            clickListener = { tempData ->
                // Handle click on item
                navigateToHomeFragment(tempData.lat, tempData.lang)
            },
            deleteListener = { tempData -> viewModel.deleteFavorite(tempData) }
        )
        binding.recyclerFavorite.adapter=adapter
        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), MapsActivity::class.java)
            val bundle = Bundle().apply {
                putString("fragment_name", "FavoriteFragment")
            }
            intent.putExtras(bundle)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = WeatherRepositoryImpl.getInstance(WeatherRemoteDataSourceImpl.getInstance(RetrofitClient.weatherApiService),
            FavoriteLocalDataSourceImo.getInstance(DatabaseClient.getInstance(requireContext()).favoriteDao()))
        val factory=FavoriteViewModelFactory(repository)
        viewModel= ViewModelProvider(this,factory)[FavoriteViewModel::class.java]

        viewModel.getFavoriteList()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteList.collect { result ->
                when (result) {
                    is ResultCallBack.Success -> {
                        adapter.submitList(result.data)
                        Log.i("FavoriteFragment", "onViewCreated: ${result.data}")
                    }
                    is ResultCallBack.Error -> {
                        Log.e("FavoriteFragment", "Error: ${result.message}")
                    }
                    ResultCallBack.Loading -> {

                    }
                }
            }
        }
        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val tempData = adapter.currentList[position]
                showDeleteConfirmationDialog(tempData)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerFavorite)

    }
    private fun showDeleteConfirmationDialog(tempData: TempData) {
        val position = adapter.currentList.indexOf(tempData)
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteFavorite(tempData)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                adapter.notifyItemChanged(position)
            }
            .show()
    }
    private fun navigateToHomeFragment(latitude: Double, longitude: Double) {
        val bundle = Bundle().apply {
            putDouble("latitude", latitude)
            putDouble("longitude", longitude)
        }

        val homeFragment = HomeFragment().apply {
            arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.favorite, homeFragment)
            .commit()
    }

}