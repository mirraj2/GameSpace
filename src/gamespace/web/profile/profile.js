$(".gamelist").each(function(index, list) {
  sync($(list));
});
syncText($(".about-me"));

if (!isMe) {
  $("h2 a").hide();
}

function syncText(component) {
  var content = component.find(".content").empty();
  var text = data.aboutMe;

  if (!text) {
    component.hide();
    return;
  }

  if (component.hasClass("editing")) {
    content.append($(
        "<textarea rows='5' placeholder=\"Tell us about your gaming experience"
            + " and which games you've been wanting to play.\">").val(text));
    content.append($("<button class='btn btn-info'>").text("Save").click(saveText));
    component.find("a").hide();
  } else {
    content.append($("<p>").html(text.replace(/\r?\n/g, "<br>")));
    component.find("a").show();
  }
}

function sync(list) {
  var content = list.find(".content").empty();
  var games = data[list.data("source")];

  if (games.length == 0) {
    if (isMe) {
      list.addClass("editing");
    }
  }

  if (list.hasClass("editing")) {
    content.append($("<textarea rows='10' placeholder='Enter games, one per line.'>").val(games.join("\n")));
    content.append($("<button class='btn btn-info'>").text("Save").click(saveList));
    list.find("a").hide();
  } else {
    var ol = $("<ol>");
    for (var i = 0; i < games.length; i++) {
      ol.append($("<li>").append($("<a href='/games/" + games[i].replace(/ /g, "-") + "'>").text(games[i])));
    }
    content.append(ol);
    list.find("a").show();
  }
}

function saveText() {
  $(this).text("Saving...").prop("disabled", true);

  var comp = $(this).parents(".about-me");
  var answer = comp.find("textarea").val().trim();
  var question = "aboutMe";
  data[question] = answer;

  $.post("/answer", {
    question : question,
    answer : answer
  }).done(function() {
    syncText(comp.toggleClass("editing"));
  });
}

function saveList() {
  $(this).text("Saving...").prop("disabled", true);

  var list = $(this).parents(".gamelist");
  var answer = list.find("textarea").val().trim();
  var question = list.data("source");
  data[question] = answer ? answer.split("\n") : [];

  $.post("/answer", {
    question : question,
    answer : answer
  }).done(function() {
    sync(list.toggleClass("editing"));
  });
}

$(".gamelist h2 a").click(function(e) {
  e.preventDefault();
  sync($(this).parents(".gamelist").toggleClass("editing"));
});

$(".about-me h2 a").click(function(e) {
  e.preventDefault();
  syncText($(this).parents(".about-me").toggleClass("editing"));
});
