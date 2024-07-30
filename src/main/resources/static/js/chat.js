let historyFetched = false;
let isBottom = true;

let receiveQueue = [];
let start = 0;

// 웹 소켓 생성, localhost:8080 부분이 Spring 서버 IP
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/chat-socket'
});

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendChat());
    $("#chat").keypress((e) => {
        if(e.code == "Enter"){
            sendChat();
        }
    });

    // 스크롤 최하단 체크
    $("#content-frame").on("scroll", ()=>{
        const frame = $("#content-frame")[0];
        if(frame.scrollTop == frame.scrollHeight-frame.clientHeight){
            isBottom = true;
        } else {
            isBottom = false;
        }
    });
});

$(window).on("beforeunload", function(){
    disconnect();
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
    fetch("http://localhost:8080/disconnect/"+$("#room").val());
    if($("#chat-container").css("display")=="block") {
        $("#chat-container").css("display", "none");
    }
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendChat() {
    if($("#chat").val()){
        stompClient.publish({
                destination: "/app/send",
                body: JSON.stringify({
                    'room' : $("#room").val(),
                    'user' : $("#user").val(),
                    'content' : $("#chat").val()
            })
        });
        $("#chat").val("");
    }
}

function fetchPreviousMessages() {
      stompClient.publish({
        destination: "/app/history/"+$("#room").val()
      });
}

function showMessage(message) {
    if($("#user").val()==message.user){
        $("#content").append("<div class=\"w-100 d-flex justify-content-end mb-2\"><div class=\"bg-warning-subtle p-1 rounded\">" + message.content + "</div></div>");
    } else {

        $("#content").append("<div class=\"w-100 d-flex justify-content-start mb-2\"><div class=\"bg-light p-1 rounded\">" + message.content + "</div></div>");
    }

    // 최하단일 경우 스크롤 고정
    if(isBottom) {
        const frame = $("#content-frame")[0];
        frame.scrollTop = frame.scrollHeight;
    }
}

function showRecentMessage(history) {
    history.forEach(item=>showMessage(item)); // 채팅 내역 출력
    receiveQueue.forEach(item=>showMessage(item)); // 채팅 로딩 중 수신내용 출력
    receiveQueue = [];
    historyFetched = true;
}

function openChat(room, opponent, user) {
    $("#room").val(room);
    $("#room-name").text(opponent);
    $("#user").val(user);

    if($("#chat-container").css("display")=="block") {
        if(room == $("#room").val()){
            console.log("already opened");
            return;
        } else {
            disconnect();
        }
    }
    $("#chat-container").css("display", "block");
    connect();
}

