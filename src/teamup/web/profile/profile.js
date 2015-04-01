$(".gamelist").each(function(index, list) {
  sync($(list));
});

function sync(list) {
  var content = list.find(".content").empty();
  var games = data[list.data("source")];

  if (list.hasClass("editing")) {
    content.append($("<textarea rows='10' placeholder='Enter games, one per line.'>").val(games.join("\n")));
    content.append($("<button class='btn btn-info'>").text("Save").click(save));
    list.find("a").hide();
  } else {
    var ol = $("<ol>");
    for (var i = 0; i < games.length; i++) {
      ol.append($("<li>").text(games[i]));
    }
    content.append(ol);
    list.find("a").show();
  }
}

function save() {
  $(this).text("Saving...").prop("disabled", true);
  
  var list = $(this).parents(".gamelist");
  var answer = list.find("textarea").val().trim();
  var question = list.data("source");
  data[question] = answer ? answer.split("\n") : [];
  
  $.post("/answer", {
    question: question,
    answer: answer
  }).done(function() {
    sync(list.toggleClass("editing"));
  });
}

$(".gamelist a").click(function(e) {
  e.preventDefault();
  sync($(this).parents(".gamelist").toggleClass("editing"));
});
