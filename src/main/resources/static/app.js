$(document).ready(function () {

	let $alert = $('#websocket-disconnected');
	let $chatMessage = $("#chat-message");
	let userId = localStorage.getItem('userId');
	let email = localStorage.getItem('email');
	let token = localStorage.getItem('token');

    if(null === token) {
        window.localStorage.clear();
        window.location.href = "/login";
    }

	let messageCount = 0;
	let rowCount = 0;
	let websocket = null;

	function setConnected(connected) {
//		$("#messages").html("");
	}

	function connect(callback) {

		let host = location.hostname + (location.port ? ':' + location.port : '');
		let wsProtocol = location.protocol === "https:" ? "wss://" : "ws://";

		websocket = new WebSocket(wsProtocol + host + "/redis-chat?token=" + token);

		websocket.onopen = openEvent => {
		    console.log('openEvent : ', openEvent);
			setConnected(true);
			callback();
		};

		websocket.onmessage = messageEvent => {
            let chatMessage = JSON.parse(messageEvent.data);
            console.log(chatMessage);
            let type = chatMessage.type;
            switch (type) {
                case 'CHAT_MESSAGE' :
                    showChatMessage(chatMessage);
                    break;
                case 'USER_JOINED' :
                    showAlertJoin(chatMessage);
                case 'USER_LEFT' :
                    showAlertLeft(chatMessage);
            }
		};

		websocket.onerror = errorEvent => {
			console.log("Error Occured.", errorEvent);
			disconnect();
		};

		websocket.onclose = closeEvent => {
			console.log("WebSocket Session Closed.", closeEvent);
			disconnect();
		};
	}

	function disconnect() {
		if (websocket !== null && websocket.readyState === websocket.OPEN) {
			websocket.close();
		}
		setConnected(false);
		window.localStorage.clear();
		console.log("Session Closed. WebSocket Disconnected.");
	}

    function showAlertJoin(chatMessage) {
        $("#messages").append(`<div class="alert_msg">
                                   <p>${'User '+chatMessage.userId+ ' joined'}</p>
                               </div>`);
    }

    function showAlertLeft(chatMessage) {
        $("#messages").append(`<div class="alert_msg">
                                   <p>${'User '+chatMessage.userId+ ' left'}</p>
                               </div>`);
    }

	function showChatMessage(chatMessage) {
		rowCount++;
		if(Number(userId) === chatMessage.userId) {
		    $("#messages").append(`<div class="outgoing_msg">
		                                <div class="sent_msg">
		                                    <span class="time_date">${'User ID : '+chatMessage.userId}</span>
		                                    <p>${chatMessage.message}</p>
		                                    <span class="time_date">${new Date(chatMessage.timestamp)}</span>
                                        </div>
                                    </div>`);
		} else {
            $("#messages").append(`<div class="incoming_msg">
                                        <div class="incoming_msg_img">
                                            <img src="https://ptetutorials.com/images/user-profile.png"alt="sunil">
                                        </div>
                                        <div class="received_msg">
                                            <div class="received_withd_msg">
                                                <span class="time_date">${'User ID : '+chatMessage.userId}</span>
                                                <p>${chatMessage.message}</p>
                                                <span class="time_date">${new Date(chatMessage.timestamp)}</span>
                                            </div>
                                        </div>
                                    </div><br/>`);
		}
		$("#messages").animate({ scrollTop: $(document).height() }, 1000);
	}

	$("#send-ws-message").click(function () {
		sendMessage();
	});

	$('#chat-message').keypress(function (e) {
		var key = e.which;
		if (key === 13) {
			sendMessage();
		}
	});

	function sendMessage() {
		if (websocket != null && websocket.readyState === websocket.OPEN) {
			let chatMessage = $chatMessage.val();
			websocket.send(JSON.stringify({
			    type: "CHAT_MESSAGE",
			    message : chatMessage
			}));
			$chatMessage.val("");
		} else {
			connect(function () {
				let chatMessage = $chatMessage.val();
				websocket.send(JSON.stringify({
                    type: "CHAT_MESSAGE",
                    message : chatMessage
                }));
				$chatMessage.val("");
			})
		}
	}

    if (websocket == null) {
        connect(function () {
            console.log("Connected");
        });
    }
});

