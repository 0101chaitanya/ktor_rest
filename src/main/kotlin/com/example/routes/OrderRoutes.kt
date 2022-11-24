package com.example.routes

import com.example.models.Order
import com.example.models.orderStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.orderRouting() {


    get("/order") {
        if (orderStorage.isNotEmpty()) {
            call.respond(orderStorage)
        } else {
            call.respondText("No orders found", status = HttpStatusCode.NotFound)
        }
    }

    get("/order/{number?}") {
        val number =
            call.parameters["number"] ?: return@get call.respondText(
                "Missing number",
                status = HttpStatusCode.BadRequest
            )
        val customer = orderStorage.find { it.number == number } ?: return@get call.respondText(
            "No order with number: $number",
            status = HttpStatusCode.NotFound
        )
        call.respond(customer)
    }
    post("/order") {
        val order = call.receive<Order>()
        orderStorage.add(order)
        call.respondText("order stored correctly", status = HttpStatusCode.Created)

    }
    delete("/order/{number?}") {
        val number = call.parameters["number"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if (orderStorage.removeIf {
                it.number == number
            }) {
            call.respondText("order removed correctly", status = HttpStatusCode.Accepted)
        } else {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
    get("/order/{number}?/total") {
        val number = call.parameters["number"] ?: return@get call.respondText(
            "Bad Request",
            status = HttpStatusCode.BadRequest
        )
        val order = orderStorage.find {
            it.number == number
        } ?: return@get (
                call.respondText("order removed correctly", status = HttpStatusCode.Accepted))
        val total = order.contents.sumOf { it.price * it.amount }
        call.respond(total)
    }

}
