import java.net.ServerSocket;

fun main() {
    val socket = ServerSocket(4221).accept()

    val input = socket.getInputStream()
    val output = socket.getOutputStream()

    input.bufferedReader().use { reader ->
        val path = reader.readLine().split(" ")[1]

        when (path) {
            "/" -> output.write("HTTP/1.1 200 OK\r\n\r\n".toByteArray())
            else -> output.write("HTTP/1.1 404 Not Found\r\n\r\n".toByteArray())
        }
    }

    output.close()
}
