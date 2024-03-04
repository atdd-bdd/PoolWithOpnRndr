@file:Suppress("SpellCheckingInspection")

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*


fun communication(client: HttpClient, messageOut: String): String {
    var text =""
    try {
        runBlocking {
            launch {
                var outputMessage = messageOut.toString()
                client.webSocket(method = HttpMethod.Get, host = "poolserver-ken.koyeb.app", port = 80, path = "/ws") {

                    if (outputMessage != "") {
                        send(outputMessage)
                        outputMessage = ""
                        val inputMessage = incoming.receive() as? Frame.Text?
                        if (inputMessage != null) {
                            text = inputMessage.readText()


                        }
                    }
                }
            }
        }
    }
    catch(e : Exception ){
        println("*** Exception in Communication *** $e")
    }
    return text
}


