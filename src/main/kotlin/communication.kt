import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*


fun communication(client: HttpClient, messageOut: Message): Message {
    var messageIn = Message()
    runBlocking {
        launch {
            var outputMessage = messageOut.toString()
//            println("Communicating $outputMessage")
            client.webSocket(method = HttpMethod.Get, host = "poolserver-ken.koyeb.app", port = 80, path = "/ws") {

//                while (true) {
                if (outputMessage != "") {
                    send(outputMessage)
                    outputMessage = ""
//                    println("Sent message")
                    val inputMessage = incoming.receive() as? Frame.Text?
                    if (inputMessage != null) {
                        val text = inputMessage.readText()
//                        println(text)
                        messageIn.fromString(text)
                    }
                }
//                        else
//                            yield()
//                    }
            }
        }
    }
    return messageIn
}


