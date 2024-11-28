interface Destinations {
    val route: String
}

object PermissionsScreen : Destinations {
    override val route: String
        get() = "PermissionsScreen"
}

object HomeScreen : Destinations {
    override val route: String
        get() = "HomeScreen"
}
