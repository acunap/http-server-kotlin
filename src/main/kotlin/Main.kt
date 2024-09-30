import java.net.ServerSocket;

fun main() {
    var serverSocket = ServerSocket(4221)
    
    serverSocket.reuseAddress = true
 
    serverSocket.accept()
    println("accepted new connection")
}
