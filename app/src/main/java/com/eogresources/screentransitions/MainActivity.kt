package com.eogresources.screentransitions

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.HiltViewModelFactory
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.eogresources.screentransitions.ui.theme.ScreenTransitionsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScreenTransitionsTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppNav()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Form : Screen("form")
    object Home : Screen("home")
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        addForm(navController)
        addHome(navController)
    }
}

private fun NavGraphBuilder.addForm(navController: NavController) {
    val deepLink = navDeepLink {
        uriPattern = "testing://one?two={two}&five={five}"
    }
    val deepLink2 = navDeepLink {
        uriPattern = "testing://three?four={four}&five={five}"
    }
    composable(
        route = "${Screen.Form.route}?five={five}",
        arguments = listOf(navArgument("five") { defaultValue = "" }),
        deepLinks = listOf(deepLink, deepLink2)
    ) { backStackEntry ->
        Log.i("Form", "Route Composable")
        val five = backStackEntry.arguments?.getString("five")
        val context = LocalContext.current

        val formViewModel: FormViewModel = viewModel(
            factory = HiltViewModelFactory(
                context,
                backStackEntry
            )
        )

        LaunchedEffect(key1 = five) {
            Log.v("Form", "Five is now $five")
            formViewModel.updateCount()
        }
        FormScreen(
            formViewModel = formViewModel,
            openHome = { navController.navigate(Screen.Home.route) }
        )
    }
}

private fun NavGraphBuilder.addHome(navController: NavController) {
    composable(Screen.Home.route) {
        Log.i("Home", "Route Composable")
        val homeViewModel = hiltViewModel<HomeViewModel>()
        HomeScreen(
            homeCount = homeViewModel.homeCount,
            updateCount = { homeViewModel.updateCount() },
            openForm = { navController.navigate(Screen.Form.route) },
            openFormForNumber = { navController.navigate("${Screen.Form.route}?five=$it") }
        )
    }
}

@Composable
fun HomeScreen(
    homeCount: Int,
    updateCount: () -> Unit,
    openForm: () -> Unit, openFormForNumber: (number: Int) -> Unit
) {
    Log.i("Home", "Screen")
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
                .fillMaxWidth()
                .height(25.dp)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Button(onClick = openForm) {
            Text("Go to Form")
        }
        Spacer(modifier = Modifier.height(25.dp))
        Button(onClick = { openFormForNumber(12) }) {
            Text("Go to Form 12")
        }
        Spacer(modifier = Modifier.height(25.dp))
        Button(onClick = { updateCount() }) {
            Text("Increment count")
        }
        Spacer(modifier = Modifier.height(25.dp))
        Text("Count is: $homeCount")
    }
}

@Composable
fun FormScreen(formViewModel: FormViewModel, openHome: () -> Unit) {
    Log.i("Form", "Screen")
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.secondary)
                .fillMaxWidth()
                .height(25.dp)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Button(onClick = openHome) {
            Text("Go Home")
        }
        Spacer(modifier = Modifier.height(25.dp))
        Text("The Count is: ${formViewModel.count}")
    }
}
