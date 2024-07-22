let historyFetched = false;

let receiveQueue = [];

// 웹 소켓 생성, localhost:8080 부분이 Spring 서버 IP
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/chat-socket'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/receive/'+$("#room").val(), (chat) => {
        if(historyFetched) {
            showMessage(JSON.parse(chat.body));
        } else {
            receiveQueue.push(JSON.parse(chat.body));
        }
    });

    stompClient.subscribe('/topic/history/'+$("#room").val(), (history) => {
        showRecentMessage(JSON.parse(history.body));
    });

    fetchPreviousMessages();
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#disconnect").prop("disabled", !connected);
    $("#content").html("");
    historyFetched = false;
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendChat() {
    stompClient.publish({
        destination: "/app/send",
        body: JSON.stringify({
            'room' : $("#room").val(),
            'user' : $("#user").val(),
            'content' : $("#chat").val()
        })
    });
}

function fetchPreviousMessages() {
      stompClient.publish({
        destination: "/app/history/"+$("#room").val()
      });
}

function showMessage(message) {
    if($("#user").val()==message.user){
        console.log(message);
        $("#content").append("<tr><td class=\"bg-primary\">" + message.content + "</td></tr>");
    } else {

        $("#content").append("<tr><td class=\"bg-secondary\">" + message.content + "</td></tr>");
    }
}

function showRecentMessage(history) {
    history.forEach(item=>showMessage(item)); // 채팅 내역 출력
    receiveQueue.forEach(item=>showMessage(item)); // 채팅 로딩 중 수신내용 출력
    receiveQueue = [];
    historyFetched = true;
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendChat());
    $("#chat").keypress((e) => sendChat())
});

$(window).on("load", function(){
    connect();
});

$(window).on("beforeunload", function(){
    disconnect();
});