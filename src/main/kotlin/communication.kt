import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*


var outputMessage = ""
var inputMessage = ""
fun communication(client: HttpClient) {
    runBlocking {
        launch {
        println("Communicating $outputMessage")
        client.webSocket(method = HttpMethod.Get, host = "poolserver-ken.koyeb.app", port = 80, path = "/ws") {

//                while (true) {
                    if (outputMessage != "") {
                        send(outputMessage)
                        outputMessage = ""
                        println("Sent message")
                        val inputMessage = incoming.receive() as? Frame.Text?
                        if (inputMessage != null) {
                            println(inputMessage.readText())
                        }
//                        else
//                            yield()
//                    }
                }
            }
        }
    }
}

