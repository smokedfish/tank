#include <wiringPi.h>
#include <wiringSerial.h>

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <math.h>
#include <signal.h>
#include <unistd.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>

void start_video() {
    system("raspivid -o - -rot 90 -t 0 -fps 20 -b 2500000 -h 1280 -w 720 | nc -l -p 5001 &");
}

void stop_video() {
    system("killall raspivid");
    system("killall nc");
}

void my_handler(int s) {
    printf("caught signal %d\n", s);
    stop_video();    
    exit(1);
}

int main(int argc , char *argv[]) {
    signal(SIGINT, my_handler);
    signal(SIGTERM, my_handler);
    signal(SIGQUIT, my_handler);
    signal(SIGHUP, my_handler);

    wiringPiSetupGpio();
    
    delay(4);

    //Create socket
    int socket_desc = socket(AF_INET , SOCK_STREAM | SOCK_NONBLOCK | SOCK_CLOEXEC , 0);
    if (socket_desc == -1) {
        printf("failed to create socket: errno=%d (%s)\n", errno, strerror(errno));
        return 1;
    }

    int optval = 1;
    setsockopt(socket_desc, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval));
    
    struct sockaddr_in server;
    memset(&server, 0, sizeof(server));
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = htonl( INADDR_ANY );
    server.sin_port = htons( 5002 );

    if (bind(socket_desc, (struct sockaddr*)&server, sizeof(server)) < 0) {
        printf("connect error: errno=%d (%s)\n", errno, strerror(errno));
        return 2;
    }

    if (listen(socket_desc, 3) == -1) {
        printf("listen error: errno=%d (%s)\n", errno, strerror(errno));
        return 3;
    }

    printf("connected");

    struct sockaddr_in client;
    int sock2 = -1;
    socklen_t len = sizeof(client);

    for (;;) {
        if (sock2 < 0) {
            sock2 = accept(socket_desc, (struct sockaddr*)&client, &len);
            start_video();
        }
        else if (sock2 >= 0) {
            int res = recv(sock2, message, 2, MSG_DONTWAIT);
            if (res < 0) {
                if (errno != EAGAIN && errno != EWOULDBLOCK) {
                    printf("Errno=%d encountered.\n", errno);
                    close(sock2);
                    sock2 = -1;                    
                    stop_video();                                  
                }
            }
            else if (res == 2) {
                int speed = message [0];
                int rotation  = message [1];                    
                printf("Data recv: speed=%d, rotation=%d\n", speed, rotation);
            }
            else {
                printf("Expected %d, got %d bytes.\n", 2, res);
                close(sock2);
                sock2 = -1;
                stop_video();
            }
        }       
        delay(10);
    }
}