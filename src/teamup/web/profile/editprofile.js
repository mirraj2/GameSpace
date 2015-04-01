$(".editing button").click(function() {
  var style = $("input[type='radio']:checked").val();
  var boardGames = $(".boardgames textarea").val();
  var digitalGames = $(".digitalgames textarea").val();

  $.post("/profile", {
    style : style,
    boardGames : boardGames,
    digitalGames : digitalGames
  }).done(function() {
    location.reload();
  });
});