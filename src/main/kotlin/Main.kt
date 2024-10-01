import java.net.ServerSocket;

fun main() {
    val socket = ServerSocket(4221).accept()

    val input = socket.getInputStream()
    val output = socket.getOutputStream()

    input.bufferedReader().use { reader ->
        val request = Request.fromRequestString(reader.readLine())

        val response = if (request.path == "/") {
            "HTTP/1.1 200 OK\r\n\r\n"
        } else {
            val urlParts = request.path.split("/")

            when (urlParts[1]) {
                "echo" -> "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: ${urlParts[2].length}\r\n\r\n${urlParts[2]}"
                else -> "HTTP/1.1 404 Not Found\r\n\r\n"
            }
        }

        output.write(response.toByteArray())
        output.close()
    }
}

data class Request(
    val method: String,
    val path: String,
    val protocol: String,
) {
    companion object {
        fun fromRequestString(requestString: String) = requestString.split(" ").let { Request(it[0], it[1], it[2]) }
    }
}
