#include <catch2/catch_all.hpp>
#include "chat_server.h"
TEST_CASE("Server starts", "[server]") {
    chatapp::ChatServer server(1234);
    server.start();
    REQUIRE(true);
}
