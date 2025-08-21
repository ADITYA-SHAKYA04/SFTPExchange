#pragma once
#include <string>
#include <vector>
namespace chatapp {
class ChatServer {
public:
    ChatServer(int port);
    void start();
    void broadcast(const std::string& message);
private:
    int port_;
    std::vector<std::string> clients_;
};
}
