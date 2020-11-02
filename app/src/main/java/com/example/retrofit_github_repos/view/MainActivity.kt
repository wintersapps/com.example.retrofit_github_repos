package com.example.retrofit_github_repos.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.retrofit_github_repos.R
import com.example.retrofit_github_repos.databinding.ActivityMainBinding
import com.example.retrofit_github_repos.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    var token: String? = null

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
        viewModel.tokenLD.observe(this, { token ->
            token?.let {
                if(token.isNotEmpty()){
                    this.token = token
                    binding.loadReposButton.isEnabled = true
                    Toast.makeText(binding.root.context, "Authentication successful", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(binding.root.context, "Authentication failed", Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.errorLD.observe(this, { message ->
            message?.let {
                Toast.makeText(binding.root.context, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun onAuthenticate() {
        val oauthUrl = getString(R.string.oauthUrl)
        val clientId = getString(R.string.clientId)
        val callBackUrl = getString(R.string.callbackUrl)
        val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("$oauthUrl?client_id=$clientId&scope=repo&redirect_uri=$callBackUrl"))
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data
        val callbackUrl = getString(R.string.callbackUrl)
        uri?.let {
            if(it.toString().startsWith(callbackUrl)){
                val code = it.getQueryParameter("code")
                code?.let {
                    val clientId = getString(R.string.clientId)
                    val clientSecret = getString(R.string.clientSecret)
                    viewModel.getToken(clientId, clientSecret, code)
                }
            }
        }
    }

    fun onLoadRepos(view: View) {

    }

    fun onPostComment(view: View) {

    }
}