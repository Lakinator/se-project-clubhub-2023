let stompClient = null;

$(document).ready(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#tryConnectButton").click(function () {
        connect();
    });
    $("#send").click(function () {
        sendMessage();
        $("#message").val("");
    });

    connect();
});

function setConnected(connected) {
    let toast;

    if (connected) {
        toast = new bootstrap.Toast($("#connectionSuccessToast"));
    } else {
        toast = new bootstrap.Toast($("#connectionFailedToast"));
    }

    toast.show();
}

function connect() {
    const socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/group-chat/new', function (message) {
            displayMessage(JSON.parse(message.body));
        });
    }, function (errMessage) {
        if (errMessage.includes("Lost connection")) {
            disconnect();
        }
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    let userId = $('input#inputUserId').val();
    let chatRoomId = $('input#inputChatRoomId').val();

    stompClient.send("/clubhub/deliver", {}, JSON.stringify(
        {
            'message': $("#message").val(),
            'userId': userId,
            'chatRoomId': chatRoomId
        }
    ));
}

function displayMessage(messageJson) {
    console.log(messageJson);

    let userId = $('input#inputUserId').val();
    let chatRoomId = $('input#inputChatRoomId').val();

    if (chatRoomId == messageJson.chatRoomId) {

        if (messageJson.userId == userId) {
            $("#messages").append("<tr><td class='text-end'>" + messageJson.content + "</td></tr>");
        } else {
            $("#messages").append("<tr><td class='text-start'>" + messageJson.userName + ": " + messageJson.content + "</td></tr>");
        }
    }
}