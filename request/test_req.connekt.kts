import java.util.Base64

val url: String by env
val basePath: String by env
val login: String by env
val password: String by env
val token: String = Base64.getEncoder().encodeToString("$login:$password".toByteArray())


GET ("$url/$basePath/service") {
    header("Accept", "application/text")
    header("Authorization", "Basic $token")
}


GET ("$url/$basePath/async") {
    header("Accept", "application/text")
    header("Authorization", "Basic $token")
}


// Create User
POST("$url/$basePath/user/create") {
    header("Content-Type", "application/json")
    header("Authorization", "Basic $token")
    body(
        """
            {
              "id": -1,
			  "username": "UserName__test",
			  "name": "Name__test",
			  "email": "Email__test@email.com"
			}
        """.trimIndent()
    )

}

