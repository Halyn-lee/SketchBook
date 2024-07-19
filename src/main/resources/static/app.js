// 웹 소켓 생성, localhost:8080 부분이 Spring 서버 IP
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/chat-socket'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/receive/'+$("#room").val(), (chat) => {
        showMessage(JSON.parse(chat.body));
    });

    stompClient.subscribe('/topic/history/'+$("#room").val(), (history) => {
        showRecentMessage(history);
    });

    //fetchPreviousMessages();
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/app/send",
        body: JSON.stringify({
            'room' : '1',
            'user' : 'temp',
            'content' : $("#name").val()
        })
    });
}

function fetchPreviousMessages() {
      stompClient.send("/app/fetchPreviousMessages", {}, {});
}

function showMessage(message) {
    $("#greetings").append("<tr><td>" + message.content + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
});