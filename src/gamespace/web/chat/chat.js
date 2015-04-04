var chatOutput = $(".chat .output");

var socket = connect("$$(chatIP)", $$(chatPort)).open(function() {
  console.log("connected!");

  socket.send({
    command : "login",
    token : $.cookie("token"),
    room : window.location.pathname
  });
}).message(handleMessage).close(function() {
  console.log("Disconnected from chat.");
});

$(".chat input").keydown(function(e) {
  if (e.keyCode == 13) {
    var text = $(this).val().trim();
    $(this).val("");
    if (text.length == 0) {
      return;
    }
    socket.send({
      command : "chat",
      text : text
    });
  }
});

function handleMessage(data) {
  var command = data.command;
  if (command == "join") {
    console.log(data);
  } else if (command == "leave") {
    console.log(data);
  } else if (command == "chat") {
    var from = $("<b>").append($("<a href='/players/" + data.fromId + "'>").text(data.from + ": "));
    var msg = $("<span>").text(data.text);
    var row = $("<div>").append(from).append(msg);
    chatOutput.append(row);
    chatOutput.scrollTop(chatOutput[0].scrollHeight);
  } else {
    console.log("Unknown command: " + command);
  }
}
