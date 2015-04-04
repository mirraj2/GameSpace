$(".event button").click(save);

function save() {
  $(".error-msg").text("");
  var data = {
    timezoneOffset : new Date().getTimezoneOffset()
  };

  $(".event [name]").each(function() {
    var input = $(this);
    var val = input.val();

    input.toggleClass("error", !val);

    if (val) {
      data[input.attr("name")] = val;
    } else {
      $(".error-msg").text("Please fill in the highlighted fields.")
    }
  });

  if (!$(".error-msg").text()) {
    $(".event button").text("Creating...").prop("disabled", true);
    $.post("/events/new", data).done(function(e) {
      window.location = e;
    }).fail(function() {
      $(".error-msg").text("There was a problem creating the event.")
    });
  }
}