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
    let message = $("#message").val().trim();

    if (message === "") {
        return;
    }

    stompClient.send("/clubhub/deliver", {}, JSON.stringify(
        {
            'message': message,
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
        let timestamp = new Date(messageJson.timestamp);
        const options = {
            dateStyle: "long",
            timeStyle: "medium"
        };
        let timestampFormatted = timestamp.toLocaleString("de-DE", options);

        if (messageJson.userId == userId) {
            $("#messages").append("<tr><td class='text-end'><p>" + messageJson.content + "</p><span class='text-muted'>" + timestampFormatted + "</span>"
                + "<a href='#' onclick='openEditModal(" + messageJson.chatMessageId + ")'><i class='ms-2 bi bi-pencil-fill'></i></a>"
                + "</td></tr>");
        } else {
            $("#messages").append("<tr><td class='text-start'><p><span class='fw-bold'>" + messageJson.userName + ":</span> "
                + messageJson.content + "</p><span class='text-muted'>" + timestampFormatted + "</span></td></tr>");
        }
    }
}

function openEditModal(messageId) {
    console.log(messageId);

    $('#edit-message-text').val("Maybe you can edit message {" + messageId + "} after paying our premium subscription fee!");

    $('#save-message-text').click(function () {
        let newText = $('#edit-message-text').val();
        console.log(newText)
        // TODO: post request to controller with data and security
    });

    $('#editMessageModal').modal("show");
}