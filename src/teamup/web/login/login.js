function statusChangeCallback(response) {
  console.log(response);
  var status = response.status;

  if (status == "connected") {
    $.post("/login", response.authResponse).done(function() {
      location = "/";
    });
  }
}

function checkLoginState() {
  FB.getLoginStatus(statusChangeCallback);
}

window.fbAsyncInit = function() {
  FB.init({
    appId : '1567602090165810',
    xfbml : true,
    version : 'v2.3'
  });

  FB.getLoginStatus(statusChangeCallback);
};

(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) {
    return;
  }
  js = d.createElement(s);
  js.id = id;
  js.src = "//connect.facebook.net/en_US/sdk.js";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));
