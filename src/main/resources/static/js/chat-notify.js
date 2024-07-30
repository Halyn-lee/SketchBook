const sse = new EventSource("http://localhost:8080/connect-chat-notify");

sse.addEventListener('connect', (e) => {
	const { data: receivedConnectData } = e;
	console.log('connect event data: ',receivedConnectData);  // "connected!"
});

sse.addEventListener('chat', (e) => {
	const { data: room } = e;
	if($("#room").val()!=room){
	    $("#circle-"+room).css("display", "block");
	}
});