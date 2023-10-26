package onenone.coding.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import onenone.coding.screen.FirstScreen

interface Route {
    val route: String
}

object FirstRoute : Route {
    override val route: String = "FirstRoute"
}

@Composable
fun MainNavGraph(
    navController: NavHostController,
    screen: Route = FirstRoute
) {
    NavHost(
        navController = navController,
        startDestination = screen.route
    ) {
        composable(FirstRoute.route, content = {
            FirstScreen()
        })
    }
}

fun NavController.navigate(screen: Route) {
    this.navigate(screen.route)
}

fun NavController.replace(screen: Route) {
    this.popBackStack()
    this.navigate(screen.route)
}