package com.example.retrofit_github_repos.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.retrofit_github_repos.R
import com.example.retrofit_github_repos.databinding.ActivityMainBinding
import com.example.retrofit_github_repos.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        binding.repositoriesSpinner.isEnabled = false
        binding.repositoriesSpinner.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("No repositories available"))
        binding.repositoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Load PullRequests
            }
        }


        binding.prsSpinner.isEnabled = false
        binding.prsSpinner.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("Please select repository"))
        binding.prsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Load comments
            }
        }


        binding.commentsSpinner.isEnabled  = false
        binding.commentsSpinner.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("Please select PR"))


        observeViewModel()
    }

    private fun observeViewModel() {

    }

    fun onAuthenticate(view: View) {

    }

    fun onLoadRepos(view: View) {

    }

    fun onPostComment(view: View) {

    }
}