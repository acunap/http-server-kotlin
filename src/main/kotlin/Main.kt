import java.io.File
import java.net.ServerSocket;
import java.util.concurrent.Executors

fun main() {
    val serverSocket = ServerSocket(4221).apply {
        reuseAddress = true
    }

    Executors.newFixedThreadPool(10).use { threadPool ->
        while (true) {
            val socket = serverSocket.accept()
            threadPool.submit {
                val input = socket.getInputStream()
                val output = socket.getOutputStream()

                input.bufferedReader().use { reader ->
                    val requestLine = reader.readLine()

                    val headerFields = generateSequence { reader.readLine() }
                        .takeWhile { it.isNotEmpty() }
                        .toList()

                    val request = Request.fromRequestString(requestLine, headerFields)

                    val response = if (request.path == "/") {
                        "HTTP/1.1 200 OK\r\n\r\n"
                    } else {
                        val urlParts = request.path.split("/")

                        when (urlParts[1]) {
                            "echo" -> "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: ${urlParts[2].length}\r\n\r\n${urlParts[2]}"
                            "user-agent" -> {
                                val userAgent = request.headers["User-Agent"] ?: throw Exception("User-Agent header missing")
                                "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: ${userAgent.length}\r\n\r\n${userAgent}"
                            }
                            "files" -> {
                                val file = File("/tmp/data/codecrafters.io/http-server-tester/${urlParts[2]}")
                                val content = file.readBytes()

                                "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: ${content.size}\r\n\r\n$content"
//
//                                if (file.exists()) {
//                                    "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: 14\r\n\r\n${file.readLines().joinToString()}"
//                                } else {
//                                    "HTTP/1.1 404 Not Found\r\n\r\n"
//                                }
                            }

                            else -> "HTTP/1.1 404 Not Found\r\n\r\n"
                        }
                    }

                    output.write(response.toByteArray())
                    output.close()
                }
            }
        }
    }
}

data class Request(
    val method: String,
    val path: String,
    val protocol: String,
    val headers: Map<String, String>
) {
    companion object {
        fun fromRequestString(requestString: String, headerFields: List<String>): Request {
            val headersMap = headerFields.map { header -> header.split(": ").let { it[0] to it[1] } }.toMap()
            return requestString.split(" ").let { Request(it[0], it[1], it[2], headersMap) }
        }
    }
}
