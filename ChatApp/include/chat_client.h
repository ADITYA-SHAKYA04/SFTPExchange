#pragma once
#include <string>
namespace chatapp {
class ChatClient {
public:
    ChatClient(const std::string& host, int port);
    void connect();
    void send(const std::string& message);
private:
    std::string host_;
    int port_;
};
}
