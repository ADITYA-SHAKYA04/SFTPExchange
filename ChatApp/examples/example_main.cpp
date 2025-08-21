#include "chat_server.h"
#include "chat_client.h"
#include <iostream>
int main() {
    chatapp::ChatServer server(8080);
    server.start();
    chatapp::ChatClient client("localhost", 8080);
    client.connect();
    client.send("Hello from client!");
    server.broadcast("Hello to all clients!");
    std::cout << "Chat example finished.\n";
    return 0;
}
