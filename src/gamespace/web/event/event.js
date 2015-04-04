$("a[href='/events']").addClass("active");

var searchUsers = new Bloodhound({
  datumTokenizer : Bloodhound.tokenizers.obj.whitespace('value'),
  queryTokenizer : Bloodhound.tokenizers.whitespace,
  remote : '/search/users/%QUERY'
});

searchUsers.initialize();

$(".typeahead").typeahead(null, {
  name : 'users',
  displayKey : 'value',
  source : searchUsers.ttAdapter()
}).bind('typeahead:selected', onSelect).bind('typeahead:autocompleted', onSelect);

$('.typeahead').on('keyup', function(e) {
  if (e.which == 13) {
    invite();
  } else {
    $(".invite button").prop("disabled", !$(this).typeahead("val"));
  }
});

var selected;
function onSelect(obj, datum, name) {
  $(this).typeahead("close");
  selected = datum;
}

$(".invite button").click(invite);

function invite() {
  var val = $(".typeahead").typeahead("val");
  if (selected != null && selected.value != val) {
    selected = null;
  }
  if (selected == null) {
    if (val) {
      if (isEmail(val)) {
        submit({
          email : val
        });
      } else {
        $(".error-msg").html("<b>" + val + "</b> isn't on GameSpace yet. Try typing in their email address instead.")
      }
    }
  } else {
    submit({
      id : selected.id
    });
  }
}

function submit(data) {
  $(".error-msg").text("");
  $(".invite button").prop("disabled", true);
  $(".typeahead").typeahead("val", "");

  $.post("/events/$$(event.id)/invite", data);
}

function isEmail(email) {
  var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
  return re.test(email);
}
