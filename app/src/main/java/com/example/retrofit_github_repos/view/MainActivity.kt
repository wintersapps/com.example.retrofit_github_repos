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
import com.example.retrofit_github_repos.model.GitHubComment
import com.example.retrofit_github_repos.model.GitHubPullRequest
import com.example.retrofit_github_repos.model.GitHubRepo
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
                if(parent?.selectedItem is GitHubRepo){
                    val currentRepo = parent.selectedItem as GitHubRepo
                    token?.let {
                        viewModel.onLoadPullRequests(it, currentRepo.owner?.login, currentRepo.name)
                    }
                }
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
                if(parent?.selectedItem is GitHubPullRequest){
                    val currentPullRequest = parent.selectedItem as GitHubPullRequest
                    val currentRepo = binding.repositoriesSpinner.selectedItem as GitHubRepo
                    token?.let {
                        viewModel.onLoadComments(it, currentRepo.owner?.login, currentRepo.name,
                                currentPullRequest.number)
                    }
                }
            }
        }


        binding.commentsSpinner.isEnabled = false
        binding.commentsSpinner.adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                arrayListOf("Please select PR"))


        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.tokenLD.observe(this, { token ->
            token?.let {
                if (token.isNotEmpty()) {
                    this.token = token
                    binding.loadReposButton.isEnabled = true
                    Toast.makeText(binding.root.context, "Authentication successful", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(binding.root.context, "Authentication failed", Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.reposLD.observe(this, { reposList ->
            reposList?.let {
                if (it.isNotEmpty()) {
                    val spinnerAdapter = ArrayAdapter(binding.root.context,
                            android.R.layout.simple_spinner_dropdown_item,
                            reposList
                    )
                    binding.repositoriesSpinner.apply {
                        adapter = spinnerAdapter
                        isEnabled = true
                    }
                } else {
                    val spinnerAdapter = ArrayAdapter(binding.root.context,
                            android.R.layout.simple_spinner_dropdown_item,
                            arrayListOf("User has no repositories")
                    )
                    binding.repositoriesSpinner.apply {
                        adapter = spinnerAdapter
                        isEnabled = false
                    }
                }
            }
        })

        viewModel.pullRequestsLD.observe(this, { pullRequestsList ->
            pullRequestsList?.let {
                if(pullRequestsList.isNotEmpty()){
                    val spinnerAdapter = ArrayAdapter(binding.root.context,
                            android.R.layout.simple_spinner_dropdown_item,
                            pullRequestsList
                    )
                    binding.prsSpinner.apply {
                        adapter = spinnerAdapter
                        isEnabled = true
                    }
                }else{
                    val spinnerAdapter = ArrayAdapter(binding.root.context,
                            android.R.layout.simple_spinner_dropdown_item,
                            arrayListOf("Repository has no pull requests")
                    )
                    binding.prsSpinner.apply {
                        adapter = spinnerAdapter
                        isEnabled = false
                    }
                }
            }
        })

        viewModel.commentsLD.observe(this, { commentsList ->
            commentsList?.let {
                if(commentsList.isNotEmpty()){
                    val spinnerAdapter = ArrayAdapter(binding.root.context,
                            android.R.layout.simple_spinner_dropdown_item,
                            commentsList
                    )
                    binding.commentsSpinner.apply {
                        adapter = spinnerAdapter
                        isEnabled = true
                    }
                    binding.commentET.isEnabled = true
                    postCommentButton.isEnabled = true
                }else{
                    val spinnerAdapter = ArrayAdapter(binding.root.context,
                            android.R.layout.simple_spinner_dropdown_item,
                            arrayListOf("Pull request has no comments")
                    )
                    binding.commentsSpinner.apply {
                        adapter = spinnerAdapter
                        isEnabled = false
                    }
                    binding.commentET.isEnabled = true
                    postCommentButton.isEnabled = true
                }
            }
        })

        viewModel.postCommentLD.observe(this, { success ->
            success?.let {
                if(success){
                    binding.commentET.text?.clear()
                    Toast.makeText(binding.root.context, "Comment created", Toast.LENGTH_LONG).show()
                    token?.let {
                        val currentRepo = binding.repositoriesSpinner.selectedItem as GitHubRepo
                        val currentPullRequest = binding.prsSpinner.selectedItem as GitHubPullRequest
                        viewModel.onLoadComments(it, currentPullRequest.user?.login, currentRepo.name,
                                currentPullRequest.number)
                    }
                }else{
                    Toast.makeText(binding.root.context, "Can not create comment", Toast.LENGTH_LONG).show()
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
            if (it.toString().startsWith(callbackUrl)) {
                val code = it.getQueryParameter("code")
                code?.let {
                    val clientId = getString(R.string.clientId)
                    val clientSecret = getString(R.string.clientSecret)
                    viewModel.getToken(clientId, clientSecret, code)
                }
            }
        }
    }

    fun onLoadRepos() {
        token?.let {
            viewModel.onLoadRepositories(it)
        }
    }

    fun onPostComment() {
        val comment = binding.commentET.text.toString()
        if(comment.isNotEmpty()){
            val currentRepo = binding.repositoriesSpinner.selectedItem as GitHubRepo
            val currentPullRequest = binding.prsSpinner.selectedItem as GitHubPullRequest
            token?.let {
                viewModel.onPostComment(it, currentRepo, currentPullRequest.number, GitHubComment(comment, null))
            }
        }else{
            Toast.makeText(binding.root.context, "Please enter a comment", Toast.LENGTH_LONG).show()
        }
    }
}