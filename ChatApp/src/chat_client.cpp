#include "chat_client.h"
#include <spdlog/spdlog.h>
namespace chatapp {
ChatClient::ChatClient(const std::string& host, int port) : host_(host), port_(port) {}
void ChatClient::connect() {
    spdlog::info("Connecting to {}:{}", host_, port_);
}
void ChatClient::send(const std::string& message) {
    spdlog::info("Sending: {}", message);
}
}
