var host = window.location.hostname;
if (host.indexOf("playwolf") != -1) {
  window.location = "http://playwolf.us:8080";
}

$(function() {
  $(".date").each(function() {
    var date = new Date($(this).text());
    var formatted = $.format.date(date, "MMMM d @ h:mm a ") + getTimeZone(date);
    $(this).text(formatted);
  });

  $(".timezone").text(getTimeZone());
});

function getTimeZone(date) {
  if (!date) {
    date = new Date();
  }
  return /\((.*)\)/.exec(date.toString())[1];
}

function reload() {
  location.reload();
}

function fail(e) {
  if (e.responseText) {
    alert(e.responseText);
  } else {
    alert("There server encountered an error.");
  }
}
