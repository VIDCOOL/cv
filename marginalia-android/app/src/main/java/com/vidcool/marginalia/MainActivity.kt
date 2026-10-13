package com.vidcool.marginalia

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.vidcool.marginalia.ui.MarginaliaNavHost
import com.vidcool.marginalia.ui.search.SearchViewModel
import com.vidcool.marginalia.ui.theme.MarginaliaTheme

class MainActivity : ComponentActivity() {
    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleShareIntent(intent)

        setContent {
            MarginaliaTheme {
                MarginaliaNavHost(viewModel = searchViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val shared = intent.getStringExtra(Intent.EXTRA_TEXT)?.trim().orEmpty()
            if (shared.isNotEmpty()) {
                searchViewModel.applySharedQuery(shared)
            }
        }
    }
}
