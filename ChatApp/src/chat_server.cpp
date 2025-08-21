#include "chat_server.h"
#include <spdlog/spdlog.h>
namespace chatapp {
ChatServer::ChatServer(int port) : port_(port) {}
void ChatServer::start() {
    spdlog::info("Server started on port {}", port_);
}
void ChatServer::broadcast(const std::string& message) {
    spdlog::info("Broadcasting: {}", message);
}
}
