#include <catch2/catch_all.hpp>
#include "chat_client.h"

TEST_CASE("Client connects", "[client]") {
    chatapp::ChatClient client("127.0.0.1", 1234);
    client.connect();
    REQUIRE(true);
}
