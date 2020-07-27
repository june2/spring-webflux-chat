$(document).ready(function () {

	let $alert = $('#websocket-disconnected');
	let $userConnected = $("#connect-alert");
	let $userDisconnect = $("#disconnect-alert");
	let $connect = $("#connect");
	let $disconnect = $("#disconnect");
	let $chatMessage = $("#chat-message");
	let userId = localStorage.getItem('userId');
	let email = localStorage.getItem('email');
	let token = localStorage.getItem('token');

    if(null === token) {
        window.localStorage.clear();
        window.location.href = "/login";
    }

	$alert.hide();
	$userConnected.hide();
	$userDisconnect.hide();

	function showUserConnectedAlert() {
		$userConnected.fadeTo(2000, 500).slideUp(500, function() {
			$userConnected.slideUp(500);
		});
	}

	function showUserDisconnectedAlert() {
		$userDisconnect.fadeTo(2000, 500).slideUp(500, function() {
			$userDisconnect.slideUp(500);
		});
	}

	let messageCount = 0;
	let rowCount = 0;
	let websocket = null;

	function setConnected(connected) {
		$connect.prop("disabled", connected);
		$disconnect.prop("disabled", !connected);
		if (connected) {
			$("#chatMessage").show();
		} else {
			$("#chatMessage").hide();
		}
		$("#messages").html("");
	}

	function connect(callback) {
		$alert.hide();

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
                    setMessageCount(chatMessage.id);
                    setUsersOnlineCount(chatMessage.usersOnline);
                    showChatMessage(chatMessage);
                    break;
                case 'USER_JOINED' :
                    setUsersOnlineCount(chatMessage.usersOnline);
                    showUserConnectedAlert();
                case 'USER_LEFT' :
                    setUsersOnlineCount(chatMessage.usersOnline);
                    showUserDisconnectedAlert();
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

		$("#messageCount").text(messageCount);
	}

	function disconnect() {
		if (websocket !== null && websocket.readyState === websocket.OPEN) {
			websocket.close();
		}
		setConnected(false);
		console.log("Session Closed. WebSocket Disconnected.");
		messageCount = 0;
		rowCount = 0;

		$alert.fadeTo(5000, 500).slideUp(500, function() {
			$alert.slideUp(500);
		});
	}

	function setMessageCount(totalCount) {
		$("#message-count").text(totalCount);
	}

	function setUsersOnlineCount(userOnline) {
		$("#users-online").text(userOnline);
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

	$disconnect.click(function () {
		console.log("Disconnect");
		disconnect();
	});

	$("#close-alert").click(function () {
		$alert.hide();
	});


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

