import java.net.ServerSocket;

fun main() {
    val socket = ServerSocket(4221).accept()
    val outputStream = socket.getOutputStream()
    outputStream.write("HTTP/1.1 200 OK\r\n\r\n".toByteArray())
    outputStream.close()
}
