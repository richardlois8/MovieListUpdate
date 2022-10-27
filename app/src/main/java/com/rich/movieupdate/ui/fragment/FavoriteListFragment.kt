package com.rich.movieupdate.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rich.movieupdate.R
import com.rich.movieupdate.adapter.FavoriteMovieAdapter
import com.rich.movieupdate.data.local.FavoriteMovie
import com.rich.movieupdate.databinding.FragmentFavoriteListBinding
import com.rich.movieupdate.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteListFragment : Fragment() {
    private lateinit var binding : FragmentFavoriteListBinding
    private lateinit var movieVM : MovieViewModel
    private lateinit var favAdapter : FavoriteMovieAdapter
    private lateinit var username : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentFavoriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movieVM = ViewModelProvider(this).get(MovieViewModel::class.java)
        username = requireActivity().getSharedPreferences("user", 0).getString("username", "")!!
        showLoading(true)
        getALlFavoriteMovie()
    }

    private fun getALlFavoriteMovie() {
        movieVM.getAllFavoriteMovie(username)
        movieVM.allFavoriteMovie.observe(viewLifecycleOwner) {
            if (it != null) {
                showLoading(false)
                showFavoriteMovies(it)
            }
        }
    }

    private fun showFavoriteMovies(data : List<FavoriteMovie>) {
        favAdapter = FavoriteMovieAdapter(data)
        binding.recViewFavorite.adapter = favAdapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val itemDecor = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.recViewFavorite.layoutManager = layoutManager
        binding.recViewFavorite.addItemDecoration(itemDecor)
        favAdapter.onClick = {
            gotoDetailMovie(it.id!!)
        }
        favAdapter.onDeleteClick = {
            deleteMovie(it)
        }
    }

    private fun deleteMovie(movie: FavoriteMovie) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Movie")
            .setMessage("Are you sure want to delete this movie from favorite?")
            .setPositiveButton("Yes") { dialog, which ->
                movieVM.deleteFavMovie(movie)
                movieVM.deleteFavoriteMovie.observe(viewLifecycleOwner){
                    if(it != null){
                        Toast.makeText(requireContext(), resources.getString(R.string.success_delete_fav), Toast.LENGTH_SHORT).show()
                        getALlFavoriteMovie()
                    }else{
                        Toast.makeText(requireContext(), resources.getString(R.string.failed_delete_fav), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    private fun gotoDetailMovie(id: Int) {
        val action = FavoriteListFragmentDirections.actionFavoriteListFragmentToDetailMovieFragment(id)
        findNavController().navigate(action)
    }

    private fun showLoading(isLoading : Boolean){
        if(isLoading){

        }else{

        }
    }
}